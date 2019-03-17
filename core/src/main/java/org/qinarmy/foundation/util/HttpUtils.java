package org.qinarmy.foundation.util;

import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * created  on 29/03/2018.
 */
public abstract class HttpUtils {

    protected HttpUtils() {

    }


    /**
     * 将参数按自然顺序排序后生成 http body
     */
    public static String createSortBody(MultiValueMap<String, String> form) {

        try {
            return doCreateSortBody( form, true );
        } catch (UnsupportedEncodingException e) {
            //不到这里
            throw new RuntimeException( e );
        }
    }

    public static String createBody(MultiValueMap<String, String> form) {
        try {
            return doCreateSortBody( form, false );
        } catch (UnsupportedEncodingException e) {
            //不到这里
            throw new RuntimeException( e );
        }
    }


    private static String doCreateSortBody(MultiValueMap<String, String> form, boolean sort) throws UnsupportedEncodingException {
        List<String> keyList = new ArrayList<>( form.keySet() );

        if (sort) {
            Collections.sort( keyList );
        }

        StringBuilder builder = new StringBuilder();
        for (Iterator<String> keyIterator = keyList.iterator(); keyIterator.hasNext(); ) {
            String key = keyIterator.next();
            List<String> valueList = form.get( key );

            if (sort) {
                Collections.sort( valueList );
            }
            for (Iterator<?> valueIterator = valueList.iterator(); valueIterator.hasNext(); ) {
                Object value = valueIterator.next();
                if (value != null) {
                    builder.append( URLEncoder.encode( key, StandardCharsets.UTF_8.name() ) );
                    builder.append( '=' );
                    builder.append( URLEncoder.encode( value.toString(), StandardCharsets.UTF_8.name() ) );
                    if (valueIterator.hasNext()) {
                        builder.append( '&' );
                    }
                }

            }
            if (keyIterator.hasNext()) {
                builder.append( '&' );
            }
        }
        return builder.toString();
    }


}
