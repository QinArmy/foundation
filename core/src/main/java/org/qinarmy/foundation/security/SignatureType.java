package org.qinarmy.foundation.security;

import org.qinarmy.army.struct.CodeEnum;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * created  on 2019-03-15.
 */
public enum SignatureType implements CodeEnum {

    NONEwithRSA(10, ""),

    MD2withRSA(20, ""),

    MD5withRSA(30, ""),

    SHA1withRSA(40, ""),

    SHA224withRSA(50, ""),

    SHA256withRSA(60, ""),

    SHA384withRSA(70, ""),

    SHA512withRSA(80, ""),

    NONEwithDSA(90, ""),

    SHA1withDSA(100, ""),

    SHA224withDSA(110, ""),

    SHA256withDSA(120, ""),

    NONEwithECDSA(130, ""),

    SHA1withECDSA(140, ""),

    SHA224withECDSA(150, ""),

    SHA256withECDSA(160, ""),

    SHA384withECDSA(170, ""),

    SHA512withECDSA(180, "");


    private final int code;

    private final String display;


    private static final Map<Integer, KeyType> CODE_MAP = CodeEnum.getCodeMap(KeyType.class);


    @Nullable
    public static KeyType resolve(int code) {
        return CODE_MAP.get(code);
    }

    SignatureType(int code, @NonNull String display) {
        this.code = code;
        this.display = display;
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
