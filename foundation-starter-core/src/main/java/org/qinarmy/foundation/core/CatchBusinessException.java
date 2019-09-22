package org.qinarmy.foundation.core;

import org.springframework.lang.NonNull;

/**
 * created  on 2019-03-13.
 */
public class CatchBusinessException extends Exception implements IBusinessException {

    private static final long serialVersionUID = 2920671092406107565L;

    private final ResultCode resultCode;

    public CatchBusinessException(@NonNull ResultCode resultCode) {
        super(resultCode.display());
        this.resultCode = resultCode;
    }

    public CatchBusinessException(@NonNull ResultCode resultCode, String format, Object... args) {
        super(IBusinessException.createMessage(format, args));
        this.resultCode = resultCode;
    }

    public CatchBusinessException(@NonNull ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(IBusinessException.createMessage(format, args), cause);
        this.resultCode = resultCode;
    }

    @Override
    public final ResultCode getResultCode() {
        return resultCode;
    }

}
