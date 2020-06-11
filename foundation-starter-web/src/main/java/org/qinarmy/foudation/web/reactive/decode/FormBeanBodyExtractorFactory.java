package org.qinarmy.foudation.web.reactive.decode;

import org.qinarmy.foudation.web.FormBean;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.web.reactive.function.BodyExtractor;
import reactor.core.publisher.Mono;

public interface FormBeanBodyExtractorFactory {

    <T extends FormBean> BodyExtractor<Mono<T>, ReactiveHttpInputMessage> toFormBean(Class<T> formBeanClass
            , Object... validationHints);
}
