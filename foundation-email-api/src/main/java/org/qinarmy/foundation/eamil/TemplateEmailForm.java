package org.qinarmy.foundation.eamil;


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
     * <p>
     * 参考 themeleaf.EngineForm#getTemplateName()
     */
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
     * 参考 org.springframework.mail.javamail.MimeMessageHelper#addInline(String, Resource)
     *
     * @see <a href="https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#mail-javamail-mime">Inline Resource</a>
     */
    @Nullable
    private List<Inline> inlineList;

    public static class Inline {

        /**
         * 表示 内容id ,second 表示 内容的资源位置.
         */
        private String contentId;

        /**
         * 表示 资源位置
         * <ul>
         * <li>{@code classpath:} 即扫本模块的 jar </li>
         * <li>{@code classpath*:} 即扫瞄路径中的所有 jar,注意这里只取第一个资源</li>
         * <li>{@code file:} 文件系统</li>
         * <li>{@code cloud:} 表示资源在云存储中</li>
         * <li>{@code env:} {@link org.springframework.core.env.Environment} 中读取</li>
         * </ul>
         */
        private String resource;

        private MediaType mediaType;

        public String getContentId() {
            return contentId;
        }

        public Inline setContentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public String getResource() {
            return resource;
        }

        public Inline setResource(String resource) {
            this.resource = resource;
            return this;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public Inline setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }
    }


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
    public List<Inline> getInlineList() {
        return inlineList;
    }

    public TemplateEmailForm setInlineList(@Nullable List<Inline> inlineList) {
        this.inlineList = inlineList;
        return this;
    }
}
