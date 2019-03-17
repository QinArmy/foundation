package org.qinarmy.foundation.core;

import org.qinarmy.army.ErrorCode;
import org.qinarmy.army.IArmyExpression;
import org.springframework.lang.NonNull;

/**
 * created  on 2019-03-13.
 */
public class RuntimeBusinessException extends RuntimeException implements IBusinessException {

    private final ResultCode resultCode;

    public RuntimeBusinessException(@NonNull ResultCode resultCode) {
        super(resultCode.display());
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(@NonNull ResultCode resultCode, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args));
        this.resultCode = resultCode;
    }

    public RuntimeBusinessException(@NonNull ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args), cause);
        this.resultCode = resultCode;
    }

    @Override
    public final ResultCode getResultCode() {
        return resultCode;
    }
}
