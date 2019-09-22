package org.qinarmy.foundation.data;


import org.qinarmy.foundation.core.ResultCode;

/**
 * created  on 2019-03-15.
 */
public class ParamValidateException extends ParamException {

    private static final long serialVersionUID = 7445147843033128545L;

    public ParamValidateException(ResultCode resultCode) {
        super(resultCode);
    }

    public ParamValidateException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public ParamValidateException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}

