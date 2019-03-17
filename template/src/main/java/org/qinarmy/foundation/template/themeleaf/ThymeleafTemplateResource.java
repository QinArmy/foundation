package org.qinarmy.foundation.template.themeleaf;

import org.qinarmy.foundation.util.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 这个类是 {@link ITemplateResource} 一个实现,主要用于 thymeleaf 定位 资源.
 * created  on 2018-12-18.
 */
public final class ThymeleafTemplateResource implements ITemplateResource {


    private final Resource resource;

    private final Charset charset;

    /**
     * @param location {@link ResourcePatternResolver#getResources(String)}
     * @throws IOException 资源未找到
     */
    public ThymeleafTemplateResource(final ApplicationContext applicationContext, String location, Charset charset)
            throws IOException {
        Validate.notNull(applicationContext, "Application Context cannot be null");
        Validate.notEmpty(location, "Resource Location cannot be null or empty");

        this.resource = applicationContext.getResources(location)[0];
        this.charset = charset;
    }

    public ThymeleafTemplateResource(Resource resource, Charset charset) {
        this.resource = resource;
        this.charset = charset;
    }

    /**
     * @param location {@link ResourcePatternResolver#getResources(String)}
     */
    public ThymeleafTemplateResource(final ApplicationContext applicationContext, String location) throws IOException {
        this(applicationContext, location, StandardCharsets.UTF_8);
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public String getBaseName() {
        return StringUtils.getFileNamePrefix(resource.getFilename());
    }

    @Override
    public boolean exists() {
        return this.resource.exists();
    }

    @Override
    public Reader reader() throws IOException {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), charset));
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        final Resource relativeResource;
        try {
            relativeResource = this.resource.createRelative(relativeLocation);
        } catch (final IOException e) {
            // Given we have delegated the createRelative(...) mechanism to Spring, it's better if we don't do
            // any assumptions on what this IOException means and simply return a resource object that returns
            // no reader and exists() == false.
            return new InvalidRelativeTemplateResource(getDescription(), relativeLocation, e);
        }
        return new ThymeleafTemplateResource(relativeResource, this.charset);
    }


    private static final class InvalidRelativeTemplateResource implements ITemplateResource {

        private final String originalResourceDescription;
        private final String relativeLocation;
        private final IOException ioException;


        InvalidRelativeTemplateResource(
                final String originalResourceDescription,
                final String relativeLocation,
                final IOException ioException) {
            super();
            this.originalResourceDescription = originalResourceDescription;
            this.relativeLocation = relativeLocation;
            this.ioException = ioException;
        }


        @Override
        public String getDescription() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation +
                    "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override
        public String getBaseName() {
            return "Invalid relative resource for relative location \"" + this.relativeLocation +
                    "\" and original resource " + this.originalResourceDescription + ": " + this.ioException.getMessage();
        }

        @Override
        public boolean exists() {
            return false;
        }

        @Override
        public Reader reader() throws IOException {
            throw new IOException("Invalid relative resource", this.ioException);
        }

        @Override
        public ITemplateResource relative(final String relativeLocation) {
            return this;
        }

        @Override
        public String toString() {
            return getDescription();
        }

    }
}
