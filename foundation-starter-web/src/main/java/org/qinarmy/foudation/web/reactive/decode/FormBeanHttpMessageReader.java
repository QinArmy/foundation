package org.qinarmy.foudation.web.reactive.decode;

import org.qinarmy.foudation.web.FormBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.DataBinder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class FormBeanHttpMessageReader<T extends FormBean> implements HttpMessageReader<T> {

    private static final List<MediaType> READABLE_MEDIA_TYPES = Collections.unmodifiableList(
            Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA)
    );

    private static final String SERVER_QUERY_PARAMS = FormBeanHttpMessageReader.class.getSimpleName()
            + ".serverQueryParams";

    private final MultipartHttpMessageReader multipartReader;

    private final FormHttpMessageReader formReader;

    public FormBeanHttpMessageReader(MultipartHttpMessageReader multipartReader, FormHttpMessageReader formReader) {
        this.multipartReader = multipartReader;
        this.formReader = formReader;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return READABLE_MEDIA_TYPES;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        Class<?> rawClass = elementType.getRawClass();
        return rawClass != null
                && FormBean.class.isAssignableFrom(rawClass)
                && (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)
                || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType))
                ;
    }

    @Override
    public Flux<T> read(ResolvableType elementType, ReactiveHttpInputMessage message
            , Map<String, Object> hints) {
        return Flux.from(readMono(elementType, message, hints));
    }

    @Override
    public Mono<T> readMono(ResolvableType elementType, ReactiveHttpInputMessage message
            , Map<String, Object> hints) {
        MediaType contentType = message.getHeaders().getContentType();

        Mono<T> mono;
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap.class
                    , String.class, String.class);
            mono = formReader.readMono(resolvableType, message, hints)
                    .map(valueMap -> createMutablePropertyValues(valueMap, hints))
                    .map(propertyValues -> bindFormBean(propertyValues, elementType))
            ;
        } else if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(MultiValueMap
                    .class, String.class, Part.class);
            mono = multipartReader.readMono(resolvableType, message, hints)
                    .map(valueMap -> createMutablePropertyValues(valueMap, hints))
                    .map(propertyValues -> bindFormBean(propertyValues, elementType))
            ;
        } else {
            throw new IllegalArgumentException(String.format("MediaType[%s] is supported by %s"
                    , contentType == null ? null : contentType.toString(), FormBeanHttpMessageReader.class.getName()));
        }
        return mono;
    }


    @Override
    public Flux<T> read(ResolvableType actualType, ResolvableType elementType, ServerHttpRequest request
            , ServerHttpResponse response, Map<String, Object> hints) {

        return read(elementType, request, obtainHints(request, hints));
    }

    @Override
    public Mono<T> readMono(ResolvableType actualType, ResolvableType elementType
            , ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        return readMono(elementType, request, obtainHints(request, hints));
    }


    /*################################## blow private method ##################################*/

    private Map<String, Object> obtainHints(ServerHttpRequest request, Map<String, Object> hints) {
        MultiValueMap<String, String> queryPrams = request.getQueryParams();
        Map<String, Object> newHints;
        if (queryPrams.isEmpty()) {
            newHints = hints;
        } else {
            newHints = new HashMap<>(hints);
            newHints.put(SERVER_QUERY_PARAMS, queryPrams);
            newHints = Collections.unmodifiableMap(newHints);
        }
        return newHints;
    }

    private MutablePropertyValues createMutablePropertyValues(MultiValueMap<String, ?> valueMap
            , Map<String, Object> hints) {

        MutablePropertyValues propertyValues = new MutablePropertyValues(valueMap);
        @SuppressWarnings("unchecked")
        MultiValueMap<String, String> queryPrams = (MultiValueMap<String, String>) hints.get(SERVER_QUERY_PARAMS);
        if (queryPrams == null) {
            return propertyValues;
        }
        for (Map.Entry<String, List<String>> e : queryPrams.entrySet()) {
            if (!propertyValues.contains(e.getKey())) {
                for (String value : e.getValue()) {
                    propertyValues.add(e.getKey(), value);
                }
            }
        }
        return propertyValues;
    }


    private T bindFormBean(MutablePropertyValues propertyValues, ResolvableType elementType) {
        //  create form bean
        Class<?> formBeanClass = elementType.getRawClass();
        Assert.notNull(formBeanClass, "elementType error");
        @SuppressWarnings("unchecked")
        T formBean = (T) BeanUtils.instantiateClass(formBeanClass);
        // bind data
        DataBinder dataBinder = new DataBinder(formBean);
        dataBinder.bind(propertyValues);
        return formBean;
    }

    /*################################## blow setter method ##################################*/

}
