package org.qinarmy.foudation.web;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;


public abstract class WebUtils extends org.springframework.web.util.WebUtils {

    protected WebUtils() {
        throw new UnsupportedOperationException();
    }


    public static MultiValueMap<String, String> parseFormData(Charset charset, String body) {
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    result.add(name, value);
                }
            }
            return result;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }


    public static MutablePropertyValues parseFormDataAsValues(Charset charset, String body, @Nullable String prefix) {
        String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(pairs.length);
        final String charsetName = charset.name();
        try {
            for (String pair : pairs) {

                String paramName, paramValue;
                int idx = pair.indexOf('=');
                if (idx < 0) {
                    paramName = URLDecoder.decode(pair, charset.name());
                    paramValue = null;
                } else {
                    paramName = URLDecoder.decode(pair.substring(0, idx), charsetName);
                    paramValue = URLDecoder.decode(pair.substring(idx + 1), charsetName);
                }
                if (prefix != null && paramName.startsWith(prefix)) {
                    paramName = paramName.substring(prefix.length());
                }
                map.add(paramName, paramValue);
            }
            return new MutablePropertyValues(map);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> HttpMessageReader<T> castHttpMessageReader(HttpMessageReader<?> reader) {
        return (HttpMessageReader<T>) reader;
    }

    /**
     * @return read-only List
     */
    public static List<Map<String, String>> createValidateErrorMessage(Errors errors
            , @Nullable MessageSource messageSource, Locale locale) {
        List<FieldError> fieldErrors = errors.getFieldErrors();
        List<Map<String, String>> errorMapList = new ArrayList<>(fieldErrors.size());

        for (FieldError fieldError : fieldErrors) {
            String message, code;
            code = fieldError.getCode();
            if (messageSource == null || code == null) {
                message = fieldError.getDefaultMessage();
            } else {
                message = messageSource.getMessage(code, fieldError.getArguments()
                        , fieldError.getDefaultMessage(), locale);
            }
            errorMapList.add(Collections.singletonMap(fieldError.getField(), message));
        }
        return Collections.unmodifiableList(errorMapList);
    }


}
