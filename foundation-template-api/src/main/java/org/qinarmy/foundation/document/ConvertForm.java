package org.qinarmy.foundation.document;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * created  on 2018-12-20.
 *
 * @see DocumentConverter
 */
public class ConvertForm {

    /**
     * 需要被转换的资源, 与 {@link #resourcePattern} 不能同时为 null.
     * 优先使用 此值 .
     */
    @Nullable
    private Resource resource;

    /**
     * 需要被转换的资源 ,与 {@link #resource} 不能同时为 null
     * 可以带有
     * 中的资源前缀.
     */
    @Nullable
    private String resourcePattern;




    @Nullable
    public Resource getResource() {
        return resource;
    }

    public ConvertForm setResource(@Nullable Resource resource) {
        this.resource = resource;
        return this;
    }

    @Nullable
    public String getResourcePattern() {
        return resourcePattern;
    }

    public ConvertForm setResourcePattern(@Nullable String resourcePattern) {
        this.resourcePattern = resourcePattern;
        return this;
    }

}
