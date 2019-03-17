package org.qinarmy.foundation.user;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2019-03-16.
 */
public enum CertificateType  implements CodeEnum {

    ID(Integer.parseInt(Constant.ID), "身份证");


    final int code;

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


    public interface Constant {

        String ID = "0";
    }



}
