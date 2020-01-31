package org.qinarmy.foundation.eamil;


import org.qinarmy.foundation.env.EnvironmentUtils;
import org.qinarmy.foundation.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import themeleaf.EngineForm;
import themeleaf.TemplateEngineException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

/**
 * 这个类是 是 {@link EmailSender} 的一个实现
 * created  on 2018/10/18.
 */
public class EmailSenderImpl implements EmailSender, EnvironmentAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderImpl.class);

    private static final String OFF = "foundation.email.off";

    private static final String SYNC = "foundation.email.sync";

    private static final String SIGNATURE = "foundation.email.signature";

    private static final String SUBJECT = "foundation.email.subject";

    private static final String META_FILE_NAME = "foundation.email.meta.fileName";

    private static final String DEFAULT_SUBJECT = "信使报信,%s";

    private JavaMailSender javaMailSender;

    private Environment env;

/*
    private TastyTemplateEngine tastyTemplateEngine;

    private CloudStore cloudStore;*/

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void send(EmailForm form) {
        assertEmailForm(form);
        if (isOff()) {
            LOG.info("email off,ignore:{}", form);
            return;
        }
        if (isOffDuration(form)) {
            LOG.info("email[{}] off", form.getSubject());
            return;
        }

        try {
            doSendSimple(form);
        } catch (EmailException e) {
            LOG.error("简单邮件发送异常", e);
            // 这里不做处理,因为邮件是用于通知,如果通知也报异常,上层将无法处理,且邮件是经过尝试的所以不处理.
        }
    }

    @Override
    public void sendTemplate(TemplateEmailForm form) {
        assertTemplateEmailForm(form);
        if (isOff()) {
            LOG.info("template email off,ignore:{}", form);
            return;
        }
        if (isOffDuration(form)) {
            LOG.info("email[{}] off", form.getSubject());
            return;
        }

        try {
            MailMessage message = createMailMessage(form);
            if (message != null) {
                doSendTemplate(form, (MimeMailMessage) message);
            }
        } catch (Exception e) {
            //throw new EmailException(ResultCode.emailSendFailure, e.getMessage(), e);
        }
    }


    private void doSendSimple(final EmailForm form) {
        assertEmailForm(form);
        try {
            MailMessage message = createMailMessage(form);

            if (message == null) {
                return;
            }
            if (message instanceof SimpleMailMessage) {

                // 发送简单邮件
                javaMailSender.send((SimpleMailMessage) message);

            } else if (message instanceof MimeMailMessage) {
                MimeMailMessage mimeMailMessage = (MimeMailMessage) message;
                // 处理附件
                processAttachment(form.getAttachmentList(), mimeMailMessage);
                // 发送多部分邮件
                javaMailSender.send(mimeMailMessage.getMimeMessage());
            } else {
                /*throw new EmailException(ResultCode.emailSendFailure
                        , String.format("未知 MailMessage[%s]", message.getClass().getName()));*/
            }
        } catch (Exception e) {
            LOG.error("简单邮件发送异常", e);
            if (!CollectionUtils.isEmpty(form.getAttachmentList())) {
                if (e instanceof EmailException) {
                    throw (EmailException) e;
                } else {
                    //  throw new EmailException(ResultCode.emailSendFailure, e.getMessage(), e);
                }
            }
        }
    }


    private void doSendTemplate(TemplateEmailForm form, MimeMailMessage message) throws MessagingException, IOException {
        //1.  处理内嵌资源,如:图片
        processInline(form.getInlineList(), message);
        //2. 处理附件
        processAttachment(form.getAttachmentList(), message);

        File emailMetaFile = FileUtils.createTempFile("txt");
        try {
            if (!form.isDropMeta()) {
                //3. 加入 元信息附件,如: 发送邮件的 主机 ip
                appendMetaAttachment(message, emailMetaFile);
            }
            //4. 执行 发送多部分邮件
            javaMailSender.send(message.getMimeMessage());
        } finally {
            FileUtils.deleteFile(emailMetaFile);
        }

    }

    /**
     * 创建邮件的主体内容
     * @return null or message
     */
    private MailMessage createMailMessage(EmailForm form) throws MessagingException, TemplateEngineException {
        final String[] to = getTo(form);

        if (ObjectUtils.isEmpty(to)) {
            LOG.info("没有 找到目标邮箱,不发送邮件,prefix[{}]", form.getPrefix());
            return null;
        }

        MailMessage message;
        if (isMimeMessage(form)) {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED_RELATED
                    , StandardCharsets.UTF_8.name());
            message = new MimeMailMessage(helper);

        } else {
            message = new SimpleMailMessage();
        }
        // 设置接收者
        message.setTo(to);
        // 设置基本的消息 内容

        basicField(message, form);
        return message;
    }

    /**
     * 设置
     * <ul>
     * <li>{@link MailMessage#setFrom(String)}</li>
     * <li>{@link MailMessage#setCc(String)}</li>
     * <li>{@link MailMessage#setBcc(String)}</li>
     * <li>{@link MailMessage#setSubject(String)}</li>
     * <li>{@link MailMessage#setSentDate(Date)}</li>
     * <li>{@link MailMessage#setText(String)}</li>
     * </ul>
     * 加入 发送主机 ip 和 端口等
     */
    private void basicField(MailMessage message, EmailForm form) throws MessagingException, TemplateEngineException {

        StringBuilder builder = new StringBuilder("Hi : all");

        appendBasic(message, form);

        if (message instanceof SimpleMailMessage) {
            appendForSimpleMessage((SimpleMailMessage) message, form, builder);

        } else if (message instanceof MimeMailMessage) {
            appendForMimeMessage((MimeMailMessage) message, form, builder);
        }

    }

    private void appendForMimeMessage(MimeMailMessage message, EmailForm form, StringBuilder builder)
            throws MessagingException, TemplateEngineException {
        MimeMessageHelper helper = message.getMimeMessageHelper();

        if (form instanceof TemplateEmailForm) {
            String html = createTemplate((TemplateEmailForm) form);
            if (StringUtils.hasText(form.getText())) {
                helper.setText(form.getText(), html);
            } else {
                helper.setText(html, true);
            }
            // html 邮件的 元信息和邮件签名 作为附近 追加在最后.

        } else if (form.getClass() == EmailForm.class) {
            if (!form.isDropMeta()) {
                /// 加入 元信息
                builder.append(createMeta())
                        .append("\n\n");
            }

            if (StringUtils.hasText(form.getText())) {
                builder.append(form.getText());
            } else {
                builder.append("以下是附件\n\n");
            }
            if (!form.isDropMeta()) {
                // 加入签名
                builder.append(getSignature());
            }
            // 文本主体 加入邮件中
            helper.setText(builder.toString());

        }
    }


    private void appendForSimpleMessage(SimpleMailMessage message, EmailForm form, StringBuilder builder) {

        if (!form.isDropMeta()) {
            // 加入 元信息
            builder.append(createMeta());
        }

        if (StringUtils.hasText(form.getText())) {
            // 加上邮件主要内容
            builder.append(form.getText());
        }

        if (form.getE() != null) {
            // 追加异常栈 信息
            builder.append("\n");
            builder.append("\n");
            builder.append(ExceptionUtils.getExceptionStack(form.getE()));
        }

        builder.append("\n");
        builder.append("\n");

        if (!form.isDropMeta()) {
            builder.append(getSignature());
        }
        message.setText(builder.toString());

    }

    /**
     * 设置
     * <ul>
     * <li>{@link MailMessage#setFrom(String)}</li>
     * <li>{@link MailMessage#setCc(String)}</li>
     * <li>{@link MailMessage#setBcc(String)}</li>
     * <li>{@link MailMessage#setSubject(String)}</li>
     * <li>{@link MailMessage#setSentDate(Date)}</li>
     * </ul>
     * 加入 发送主机 ip 和 端口等
     */

    private void appendBasic(MailMessage message, EmailForm form) {
        // 邮件由谁发送
        message.setFrom(getFrom());
        // 邮件抄送给谁
        message.setCc(getCc(form));

        // 邮件暗抄给谁
        message.setBcc(getBcc(form));

        // 主题
        if (StringUtils.hasText(form.getSubject())) {
            message.setSubject(form.getSubject());
        } else {
            message.setSubject(getDefaultSubject());
        }
        LocalDateTime now = LocalDateTime.now();
        // 发送时间
        LocalDateTime sendTime = form.getDateTime() == null ? now : form.getDateTime();
        message.setSentDate(Date.from(sendTime.toInstant(TimeUtils.ZONE_OFFSET8)));
    }


    private String createMeta() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n")
                .append("\n 服务器结点> ")
                .append(getLocalAddress())
                .append(":")
                .append(getPort())
                .append(" ; env>")
                // .append(env.getRequiredProperty(EnvConstant.ENV))
                .append(" ; 线程>")
                .append(getThreadName())
                .append(" ;  应用> ")
                .append(getApp())
                .append(" ;  邮件生成时间> ")
        ;

        LocalDateTime now = LocalDateTime.now();

        builder.append(now.format(TimeUtils.DATE_TIME_FORMATTER));

        builder.append("\n");
        builder.append("\n");

        return builder.toString();
    }


    private String[] getTo(EmailForm form) {
        String[] to = ArrayUtils.EMPTY_STRING_ARRAY;
        if (StringUtils.hasText(form.getPrefix())) {
            to = env.getProperty(form.getPrefix() + ".to", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return to;
    }

    private String[] getCc(EmailForm form) {
        String[] to = ArrayUtils.EMPTY_STRING_ARRAY;
        if (StringUtils.hasText(form.getPrefix())) {
            to = env.getProperty(form.getPrefix() + ".cc", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return to;
    }

    private String[] getBcc(EmailForm form) {
        String[] to = ArrayUtils.EMPTY_STRING_ARRAY;

        if (StringUtils.hasText(form.getPrefix())) {
            to = env.getProperty(form.getPrefix() + ".bcc", String[].class, ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return to;
    }

    private boolean isOffDuration(EmailForm form) {
        return EnvironmentUtils.isOffDuration(form.getPrefix() + ".off.duration");
    }


    /**
     * 处理附件
     */
    private void processAttachment(List<EmailForm.Attachment> attachmentList, MimeMailMessage message)
            throws EmailException, IOException, MessagingException {
        if (CollectionUtils.isEmpty(attachmentList)) {
            return;
        }
        MimeMessageHelper helper = message.getMimeMessageHelper();

        Resource resource;
        String fileName;
        for (EmailForm.Attachment attachment : attachmentList) {
            Assert.hasText(attachment.getFileName(), "file name required");
            Assert.isTrue(attachment.getFileName().length() <= 20, "附件名已超过20个字符.");
            Assert.hasText(attachment.getResource(), "resource pattern required");
            Assert.notNull(attachment.getMediaType(), "MediaType required");

            resource = loadResource(attachment.getResource());
            fileName = MimeUtility.encodeText(attachment.getFileName(), StandardCharsets.UTF_8.name(), "B");
            helper.addAttachment(fileName, resource, attachment.getMediaType().toString());

        }

    }

    /**
     * 为 html 邮件追加元信息附件
     */
    private void appendMetaAttachment(MimeMailMessage message, File emailMetaFile) throws MessagingException, IOException {
        MimeMessageHelper helper = message.getMimeMessageHelper();

        String fileName = MimeUtility.encodeText(env.getProperty(META_FILE_NAME, "emailMeta.txt")
                , StandardCharsets.UTF_8.name(), "B");

        String metaContent = createMeta() + "\n\n" + getSignature();

        try (FileWriter writer = new FileWriter(emailMetaFile)) {
            writer.append(metaContent);
        }
        Resource resource = new FileSystemResource(emailMetaFile);

        helper.addAttachment(fileName, resource, MediaType.TEXT_PLAIN.toString());
    }


    private void processInline(List<TemplateEmailForm.Inline> inlineList, MimeMailMessage message)
            throws IOException, EmailException, MessagingException {
        if (CollectionUtils.isEmpty(inlineList)) {
            return;
        }
        MimeMessageHelper helper = message.getMimeMessageHelper();

        Resource resource;
        for (TemplateEmailForm.Inline inline : inlineList) {
            Assert.notNull(inline.getContentId(), "contentId  required");
            Assert.hasText(inline.getResource(), "resource pattern required");
            Assert.notNull(inline.getMediaType(), "MediaType required");


            resource = loadResource(inline.getResource());

            helper.addInline(inline.getContentId(), resource, inline.getMediaType().toString());

        }

    }


    private Resource loadResource(final String pattern) throws EmailException, IOException {

        if (pattern.startsWith(ResourceUtils.ENV_PREFIX)) {
            return loadEnvResource(pattern);
        }
        Resource resource;
        if (pattern.startsWith(ResourceUtils.CLOUD_PREFIX)) {

            resource = loadCloudResource(pattern);
        } else {
            Resource[] resources = ResourceUtils.getResources(pattern);
            if (ObjectUtils.isEmpty(resources)) {
               /* throw new EmailException(ResultCode.emailSendFailure
                        , String.format("未找到资源,pattern[%s]", pattern));*/
            }
            resource = resources[0];
        }
        return resource;
    }


    /**
     * @throws IllegalStateException 没有相应的 env 资源
     */
    private Resource loadEnvResource(final String envResource) throws EmailException, IOException {
        String resourcePattern = envResource;
        for (; ; ) {
            if (resourcePattern.startsWith(ResourceUtils.ENV_PREFIX)) {
                resourcePattern = env.getRequiredProperty(resourcePattern);
            } else {
                break;
            }
        }
        Resource resource;
        if (ResourceUtils.containsPrefix(resourcePattern)) {
            resource = loadResource(resourcePattern);
        } else {
            resource = new ByteArrayResource(resourcePattern.getBytes(StandardCharsets.UTF_8));
        }
        return resource;
    }


    private Resource loadCloudResource(final String pattern) throws EmailException {
       /* try {
            String resourcePattern = pattern;
            if (pattern.startsWith(ResourceUtils.CLOUD_PREFIX)) {
                resourcePattern = pattern.substring(ResourceUtils.CLOUD_PREFIX.length());
            }
            DownloadResult result = cloudStore.download(resourcePattern);

            return new InputStreamResource(result.getInputStream());
        } catch (CloudStoreException | DataException e) {
            String msg = String.format("加载云资源出错,请确认云资源,pattern[%s]", pattern);
            throw new EmailException(ResultCode.emailSendFailure, msg, e);
        }*/
        return null;
    }


    private boolean isMimeMessage(EmailForm form) {
        return (form instanceof TemplateEmailForm) || !CollectionUtils.isEmpty(form.getAttachmentList());
    }


    private void assertEmailForm(EmailForm form) {
        assertCommon(form);

    }

    private void assertCommon(EmailForm form) {

        boolean error = StringUtils.hasText(form.getPrefix()) && !form.getPrefix().endsWith(".email");
        Assert.isTrue(!error, "prefix must end with .email");

    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    private void assertTemplateEmailForm(TemplateEmailForm form) {

        assertCommon(form);
        Assert.hasText(form.getTemplatePattern(), "templatePattern required");
        Assert.notNull(form.getVariables(), "variables required");
    }


    private String createTemplate(TemplateEmailForm form) throws TemplateEngineException {
        EngineForm engineForm = new EngineForm()
                .setTemplateName(form.getTemplatePattern())
                .setVariables(form.getVariables());
        // return tastyTemplateEngine.renderAsString(engineForm);
        return null;
    }


    private String getLocalAddress() {
        String address = null;
        try {
            // address = NetUtils.getPrivateAddrAndPrecedenceV4().getHostAddress();
        } catch (RuntimeException e) {
            address = "未知主机";
        }
        return address;
    }

    private String getApp() {
        return env.getRequiredProperty("spring.application.name");
    }

    private boolean isSync() {
        return env.getProperty(SYNC, Boolean.class, Boolean.TRUE);
    }

    /**
     * @return 本系统监听的端口
     */
    private int getPort() {
        return  env.getProperty("server.port", Integer.class, 0);
    }


    private boolean isOff() {
        return env.getProperty(OFF, Boolean.class, Boolean.FALSE);
    }

    private String getSignature() {
        String signature = env.getProperty(SIGNATURE);
        if (!StringUtils.hasText(signature)) {
            signature = getDefaultSignature();
        }
        return signature;
    }

    private String getDefaultSubject() {
        String subject = env.getProperty(SUBJECT, DEFAULT_SUBJECT);
        if (subject.contains("%s")) {
            subject = String.format(subject, LocalDateTime.now().format(TimeUtils.DATE_TIME_FORMATTER));
        }
        return subject;
    }

    private String getFrom() {
        return env.getRequiredProperty("spring.mail.username");
    }

    private String getDefaultSignature() {
        return "\n\n系统信使" +
                "\n" +
                getFrom() +
                "\n" +
                "地址" +
                "\n\n";
    }

    @Autowired
    public void setJavaMailSender(@Qualifier("mailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


}
