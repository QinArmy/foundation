package org.qinarmy.foundation.eamil;


/**
 * 邮件发送器
 * created  on 2018/10/18.
 */
public interface EmailSender {


    /**
     * 发送简单邮件,通常用于发警报,所以有网络问题失败,不抛出异常
     * <p>
     * 若发送附件且出错时(附件未找到,网络问题), 则会抛出异常
     * </p>
     */
    void send(EmailForm form);

    /**
     * 在这个方法 {@link TemplateEmailForm#getText()} 会被忽略
     * 发送指定模块的邮件
     */
    void sendTemplate(TemplateEmailForm form) throws EmailException;


}
