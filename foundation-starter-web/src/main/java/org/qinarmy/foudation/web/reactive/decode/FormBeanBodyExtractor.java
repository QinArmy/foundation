package org.qinarmy.foudation.web.reactive.decode;

import org.qinarmy.foudation.web.FormBean;
import org.qinarmy.foudation.web.WebUtils;
import org.qinarmy.foundation.bind.DataValidateException;
import org.qinarmy.foundation.util.JsonUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final class FormBeanBodyExtractor<T extends FormBean> implements BodyExtractor<Mono<T>, ReactiveHttpInputMessage> {


    private final Class<T> elementClass;

    private final Object[] validationHints;

    private Validator webFluxValidator;

    private MessageSource messageSource;


    FormBeanBodyExtractor(Class<T> elementClass, @Nullable Object[] validationHints) {
        this.elementClass = elementClass;
        this.validationHints = validationHints;
    }

    @Override
    public Mono<T> extract(ReactiveHttpInputMessage message, Context context) {

        final MediaType contentType = obtainContentType(message);

        final ResolvableType elementType = ResolvableType.forClass(this.elementClass);

        return Flux.fromIterable(context.messageReaders())
                .filter(reader -> reader.canRead(elementType, contentType))
                .next()
                .switchIfEmpty(notFoundHttpMessageReader(elementType, message, context))
                .map(WebUtils::<T>castHttpMessageReader)
                .flatMap(messageReader -> readMessage(elementType, messageReader, message, context))
                ;
    }

    private Mono<T> readMessage(ResolvableType elementType, HttpMessageReader<T> reader
            , ReactiveHttpInputMessage message, BodyExtractor.Context context) {

        return context.serverResponse()
                .map(response -> reader.readMono(elementType, elementType, (ServerHttpRequest) message, response
                        , context.hints()))
                .orElseGet(() -> reader.readMono(elementType, message, context.hints()))
                .flatMap(this::validateFormBean);
    }

    private Mono<HttpMessageReader<?>> notFoundHttpMessageReader(ResolvableType elementType
            , ReactiveHttpInputMessage inputMessage, BodyExtractor.Context context) {
        return Mono.defer(() -> {
            List<MediaType> mediaTypes = context.messageReaders().stream()
                    .flatMap(reader -> reader.getReadableMediaTypes().stream())
                    .collect(Collectors.toList());

            return Mono.from(unsupportedErrorHandler(inputMessage
                    , new UnsupportedMediaTypeException(obtainContentType(inputMessage), mediaTypes, elementType)));
        });
    }

    private MediaType obtainContentType(ReactiveHttpInputMessage inputMessage) {
        return Optional.ofNullable(inputMessage.getHeaders().getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private Mono<T> validateFormBean(T formBean) {
        Validator validator = this.webFluxValidator;
        if (validator == null) {
            return Mono.just(formBean);
        }
        BeanPropertyBindingResult errors;
        errors = new BeanPropertyBindingResult(formBean, formBean.getClass().getSimpleName());
        if (validator instanceof SmartValidator && validationHints != null) {
            ((SmartValidator) validator).validate(formBean, errors, validationHints);
        } else {
            validator.validate(formBean, errors);
        }
        Mono<T> mono;
        if (errors.hasErrors()) {
            List<Map<String, String>> errorMessageList;
            errorMessageList = WebUtils.createValidateErrorMessage(errors, messageSource, Locale.getDefault());
            mono = Mono.error(new DataValidateException(JsonUtils.writeValue(errorMessageList)));
        } else {
            mono = Mono.just(formBean);
        }
        return mono;
    }


    /*################################## blow setter method ##################################*/

    public FormBeanBodyExtractor<T> setWebFluxValidator(Validator webFluxValidator) {
        this.webFluxValidator = webFluxValidator;
        return this;
    }

    public FormBeanBodyExtractor<T> setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }

    /*################################## blow private static method ##################################*/

    private static <T> Flux<T> unsupportedErrorHandler(
            ReactiveHttpInputMessage message, UnsupportedMediaTypeException ex) {

        Flux<T> result;
        if (message.getHeaders().getContentType() == null) {
            // Maybe it's okay there is no content type, if there is no content..
            result = message.getBody().map(buffer -> {
                DataBufferUtils.release(buffer);
                throw ex;
            });
        } else {
            result = message instanceof ClientHttpResponse ?
                    consumeAndCancel(message).thenMany(Flux.error(ex)) : Flux.error(ex);
        }
        return result;
    }

    private static Mono<Void> consumeAndCancel(ReactiveHttpInputMessage message) {
        return message.getBody()
                .map(buffer -> {
                    DataBufferUtils.release(buffer);
                    throw new ReadCancellationException();
                })
                .onErrorResume(ReadCancellationException.class, ex -> Mono.empty())
                .then();
    }

    @SuppressWarnings("serial")
    private static class ReadCancellationException extends RuntimeException {
    }
}
