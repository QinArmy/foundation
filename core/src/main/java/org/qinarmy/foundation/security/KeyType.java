package org.qinarmy.foundation.security;

import org.qinarmy.army.struct.CodeEnum;
import org.qinarmy.foundation.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * created  on 2019-03-13.
 * @see KeyUtils
 */
public enum KeyType implements CodeEnum {

    AES(100, "AES", ArrayUtils.asSet(128) )

   ;


    private final int code;

    private final String display;

    public final Collection<Integer> keySizes;

    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.getCodeMap(KeyType.class);


    @Nullable
    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    KeyType(int code, @NonNull String display, @NonNull Collection<Integer> keySizes) {
        this.code = code;
        this.display = display;
        this.keySizes = Collections.unmodifiableCollection(keySizes);
    }

    @Override
    public int code() {
        return code;
    }

    @NonNull
    @Override
    public String display() {
        return display;
    }

}
