package org.qinarmy.foundation.core;

import org.qinarmy.army.IArmyExpression;
import org.springframework.lang.NonNull;

/**
 * created  on 2019-03-13.
 */
public class CatchBusinessException extends Exception implements IBusinessException {

    private final ResultCode resultCode;

    public CatchBusinessException(@NonNull ResultCode resultCode) {
        super(resultCode.display());
        this.resultCode = resultCode;
    }

    public CatchBusinessException(@NonNull ResultCode resultCode, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args));
        this.resultCode = resultCode;
    }

    public CatchBusinessException(@NonNull ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args), cause);
        this.resultCode = resultCode;
    }

    @Override
    public final ResultCode getResultCode() {
        return resultCode;
    }

}
