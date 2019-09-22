package org.qinarmy.foundation.security;


import org.qinarmy.foundation.struct.CodeEnum;
import org.qinarmy.foundation.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * created  on 2019-03-13.
 */
public enum KeyPairType implements CodeEnum {

    RSA(200, "RSA", ArrayUtils.asSet(1024, 2048, 4096));


    private final int code;

    private final String display;

    public final Collection<Integer> keySizes;

    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.getCodeMap(KeyType.class);


    @Nullable
    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    KeyPairType(int code, @NonNull String display, @NonNull Collection<Integer> keySizes) {
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
