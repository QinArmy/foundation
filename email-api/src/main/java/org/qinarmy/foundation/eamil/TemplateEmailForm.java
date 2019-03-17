package org.qinarmy.foundation.eamil;


import org.qinarmy.army.util.Triple;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * created  on 2018-12-24.
 */
public class TemplateEmailForm extends EmailForm {

    /**
     * 模板资源模式
     *
     *
     */
    // EngineForm#getTemplateName()
    @NonNull
    private String templatePattern;

    /**
     * 模板渲染需要的变量
     */
    @NonNull
    private Map<String, Object> variables;

    /**
     * 放放邮件内容中的资源,如:内嵌的图片.
     * <p>
     * first 表示 内容id ,second 表示 内容的资源位置.
     * second 可有以下前缀, third 表示内容类型
     * <ul>
     * <li>{@code classpath:} 即扫本模块的 jar </li>
     * <li>{@code classpath*:} 即扫瞄路径中的所有 jar </li>
     * <li>{@code file:} 文件系统</li>
     * <li>{@code env:} {@link org.springframework.core.env.Environment} 中读取</li>
     * </ul>
     * </p>
     *
     *
     * @see <a href="https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#mail-javamail-mime">Inline Resource</a>
     */
    //org.springframework.mail.javamail.MimeMessageHelper#addInline(String, Resource)
    @Nullable
    private List<Triple<String, String, MediaType>> inlineList;


    @NonNull
    public String getTemplatePattern() {
        return templatePattern;
    }

    public TemplateEmailForm setTemplatePattern(@NonNull String templatePattern) {
        this.templatePattern = templatePattern;
        return this;
    }

    @NonNull
    public Map<String, Object> getVariables() {
        return variables;
    }

    public TemplateEmailForm setVariables(@NonNull Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    @Nullable
    public List<Triple<String, String, MediaType>> getInlineList() {
        return inlineList;
    }

    public TemplateEmailForm setInlineList(@Nullable List<Triple<String, String, MediaType>> inlineList) {
        this.inlineList = inlineList;
        return this;
    }
}
