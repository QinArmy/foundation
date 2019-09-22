package org.qinarmy.foundation.struct.user;

import org.qinarmy.foundation.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2019-03-16.
 */
public enum CertificateType implements CodeEnum {

    NONE(0, "无"),
    ID(100, "身份证"),
    ENTERPRISE(200, "企业营业证");

    private final int code;

    private final String display;


    private static final Map<Integer, CertificateType> CODE_MAP = CodeEnum.getCodeMap(CertificateType.class);


    public static CertificateType resolve(int code) {
        return CODE_MAP.get(code);
    }

    CertificateType(int code, String display) {
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
