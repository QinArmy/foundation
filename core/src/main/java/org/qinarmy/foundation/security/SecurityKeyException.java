package org.qinarmy.foundation.security;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * created  on 2019-03-13.
 */
public class SecurityKeyException extends RuntimeBusinessException {

    public SecurityKeyException(ResultCode resultCode) {
        super(resultCode);
    }

    public SecurityKeyException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public SecurityKeyException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}
