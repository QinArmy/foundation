package org.qinarmy.foundation.user;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2019-03-16.
 */
public enum Gender implements CodeEnum {

    NONE(0,"无"),
    MALE(100,"男性"),
    FEMALE(200,"女性")
    ;


    final int code;

    private final String display;


    private static final Map<Integer, Gender> CODE_MAP = CodeEnum.getCodeMap(Gender.class);


    public static Gender resolve(int code) {
        return CODE_MAP.get(code);
    }

    Gender(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }
}
