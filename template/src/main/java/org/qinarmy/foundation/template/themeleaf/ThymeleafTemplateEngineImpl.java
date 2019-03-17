package org.qinarmy.foundation.template.themeleaf;


import org.hibernate.exception.DataException;
import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.data.ParamValidateException;
import org.qinarmy.foundation.document.ConvertForm;
import org.qinarmy.foundation.document.DocumentConverter;
import org.qinarmy.foundation.util.BeanUtils;
import org.qinarmy.foundation.util.FileUtils;
import org.qinarmy.foundation.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.util.FastStringWriter;
import themeleaf.EngineForm;
import themeleaf.TemplateEngineException;
import themeleaf.ThymeleafTemplateEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static org.qinarmy.foundation.util.Assert.*;
import static org.qinarmy.foundation.util.ResourceUtils.ENV_PREFIX;


/**
 * 这个类是 {@link ThymeleafTemplateEngine} 的一个实现.
 * created  on 2018-12-19.
 *
 * @see DocumentConverter
 */
public class ThymeleafTemplateEngineImpl implements ThymeleafTemplateEngine, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ThymeleafTemplateEngineImpl.class);


    private Environment env;

    private ITemplateEngine templateEngine;

    private DocumentConverter documentConverter;


    private enum ResultType {
        HTML,
        PDF,
        STRING

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Nullable
    @Override
    public Resource render(@NonNull EngineForm form) throws TemplateEngineException, ParamValidateException {
        return process(form, ResultType.HTML);
    }

    @Nullable
    @Override
    public Resource renderAsPdf(@NonNull EngineForm form) throws TemplateEngineException, ParamValidateException {
        return process(form, ResultType.PDF);
    }

    @Nullable
    @Override
    public String renderAsString(@NonNull EngineForm form) throws TemplateEngineException, ParamValidateException {

        assertForm(form);

        try {
            FastStringWriter writer = new FastStringWriter();
            // 替换模板
            process(computeTemplateName(form.getTemplateName()), createContext(form), writer);

            String result = writer.toString();

            if (!StringUtils.hasText(result)) {
                throw new TemplateEngineException(ResultCode.HTML_TEMPLATE,
                        String.format("没有找到相应模板,template[%s]", form.getTemplateName()));
            }
            return result;
        } catch (TemplateEngineException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateEngineException(ResultCode.HTML_TEMPLATE, e.getMessage(), e);
        }
    }



    /*########################## 以下非接口方法 #####################################*/


    private void assertForm(EngineForm form) throws ParamValidateException {
        assertHasText(form.getTemplateName(), "templateName required");
        if (form.getTemplateName().startsWith(ENV_PREFIX)) {
            assertTrue(form.getTemplateName().length() > ENV_PREFIX.length(), "templateName error");
        }
        boolean bothNull = form.getVariables() == null && form.getVariableBean() == null;
        assertFalse(bothNull, "variables and variableBean both is nul");

    }


    /**
     * @param form       required
     * @param resultType required
     * @return null or resource
     */
    private Resource process(EngineForm form, ResultType resultType)
            throws ParamValidateException, TemplateEngineException {
        assertForm(form);
        String templateName = computeTemplateName(form.getTemplateName());
        IContext context = createContext(form);
        Resource resource;
        try {
            switch (resultType) {
                case HTML:
                    File tempFile = createTempFile();
                    process(templateName, context, new FileWriter(tempFile));
                    resource = new FileSystemResource(tempFile);
                    break;
                case PDF:
                    resource = processAsPdf(templateName, context);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            return resource;
        } catch (Exception e) {
            throw new TemplateEngineException(ResultCode.HTML_TEMPLATE, e.getMessage(), e);
        }
    }

    /**
     * 调用模板引擎
     */
    private void process(String templateName, IContext context, Writer writer) throws TemplateEngineException {
        try (Writer w = writer) {
            templateEngine.process(templateName, context, w);
        } catch (IOException e) {
            throw new TemplateEngineException(ResultCode.HTML_TEMPLATE, e.getMessage(), e);
        }
    }


    private Resource processAsPdf(String templateName, IContext context) throws IOException {
        File html = createTempFile();

        try {
            FileWriter w = new FileWriter(html);
            process(templateName, context, w);
            ConvertForm form = new ConvertForm()
                    .setResource(new FileSystemResource(html));
            return documentConverter.htmlToPdf(form);
        } finally {
            FileUtils.deleteFile(html);
        }
    }


    private File createTempFile() throws IOException {
        File dir = new File(FileUtils.getTempDirWithDate(), "thymeleaf");
        if (!dir.exists() && dir.mkdir()) {
            LOG.debug("create temp dir {}", dir.getAbsolutePath());
        }
        File file = new File(dir, UUID.randomUUID().toString());
        if (!file.exists() && file.createNewFile()) {
            LOG.debug("create temp file {}", file.getAbsolutePath());
        }
        return file;
    }

    private IContext createContext(EngineForm form) {
        IContext context;
        if (form.getVariables() != null) {
            context = new Context(Locale.CHINA, form.getVariables());
        } else {
            context = new Context(Locale.CHINA, BeanUtils.copyToMap(Objects.requireNonNull(form.getVariableBean())));
        }
        return context;
    }

    private String computeTemplateName(String template) throws DataException {
        String templateName = template;
        if (template.startsWith(ENV_PREFIX)) {
            templateName = env.getProperty(template.substring(ENV_PREFIX.length()));
            assertHasText(templateName, "%s not config.", template);
        }
        return templateName;
    }


    /*############################ 以下是依赖 setter ###################################*/


    @Autowired
    public void setTemplateEngine(@Qualifier("templateEngine") ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    public void setDocumentConverter(@Qualifier("documentConverter") DocumentConverter documentConverter) {
        this.documentConverter = documentConverter;
    }
}
