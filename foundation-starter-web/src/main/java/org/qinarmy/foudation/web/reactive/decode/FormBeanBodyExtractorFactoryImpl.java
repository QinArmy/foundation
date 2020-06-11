package org.qinarmy.foudation.web.reactive.decode;

import org.qinarmy.foudation.web.FormBean;
import org.springframework.context.MessageSource;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyExtractor;
import reactor.core.publisher.Mono;

public class FormBeanBodyExtractorFactoryImpl implements FormBeanBodyExtractorFactory {

    private Validator webFluxValidator;

    private MessageSource messageSource;


    @Override
    public <T extends FormBean> BodyExtractor<Mono<T>, ReactiveHttpInputMessage> toFormBean(Class<T> formBeanClass
            , Object... validationHints) {
        return new FormBeanBodyExtractor<>(formBeanClass, validationHints)
                .setWebFluxValidator(webFluxValidator)
                .setMessageSource(messageSource)
                ;
    }

    public FormBeanBodyExtractorFactoryImpl setWebFluxValidator(Validator webFluxValidator) {
        this.webFluxValidator = webFluxValidator;
        return this;
    }

    public FormBeanBodyExtractorFactoryImpl setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }
}
