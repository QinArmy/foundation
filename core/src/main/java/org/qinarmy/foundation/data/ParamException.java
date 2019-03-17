package org.qinarmy.foundation.data;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * created  on 2019-03-17.
 */
public abstract class ParamException extends RuntimeBusinessException {

    private static final long serialVersionUID = -8805586282992220081L;

    public ParamException(ResultCode resultCode) {
        super(resultCode);
    }

    public ParamException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public ParamException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }

}
