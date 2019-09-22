package org.qinarmy.foundation.core;


import org.springframework.lang.NonNull;

/**
 * created  on 2019-03-13.
 */
public class RuntimeBusinessException extends RuntimeException implements IBusinessException {

    private static final long serialVersionUID = 4977127946615943965L;

    private final ResultCode resultCode;

    public RuntimeBusinessException(@NonNull ResultCode resultCode) {
        super(resultCode.display());
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(@NonNull ResultCode resultCode, String format, Object... args) {
        super(IBusinessException.createMessage(format, args));
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(@NonNull ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(IBusinessException.createMessage(format, args), cause);
        this.resultCode = resultCode;
    }

    @Override
    public final ResultCode getResultCode() {
        return resultCode;
    }
}
