package org.qinarmy.foundation.tx;

import org.qinarmy.foundation.core.ResultCode;

/**
 * created  on 2019-03-17.
 */
public class OptimisticLockingException extends ArmyDataAccessException {

    private static final long serialVersionUID = -2630535738094562182L;

    public OptimisticLockingException( String format, Object... args) {
        super(ResultCode.OPTIMISTIC_LOCKING, format, args);
    }

    public OptimisticLockingException( Throwable cause, String format, Object... args) {
        super(ResultCode.OPTIMISTIC_LOCKING, cause, format, args);
    }
}
