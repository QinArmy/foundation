package org.qinarmy.foundation.eamil;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * 邮件发送出错时抛出,
 * created  on 2018-12-24.
 */
public class EmailException extends RuntimeBusinessException {


    private static final long serialVersionUID = -5544485988474758697L;

    public EmailException(ResultCode resultCode) {
        super(resultCode);
    }

    public EmailException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public EmailException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}
