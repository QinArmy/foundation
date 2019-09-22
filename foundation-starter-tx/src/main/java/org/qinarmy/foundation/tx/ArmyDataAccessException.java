package org.qinarmy.foundation.tx;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * created  on 2019-03-13.
 * org.springframework.dao.DataAccessException
 */
public class ArmyDataAccessException extends RuntimeBusinessException {


    public ArmyDataAccessException(ResultCode resultCode) {
        super(resultCode);
    }

    public ArmyDataAccessException(ResultCode resultCode, String format, Object... args) {
        super(resultCode, format, args);
    }

    public ArmyDataAccessException(ResultCode resultCode, Throwable cause, String format, Object... args) {
        super(resultCode, cause, format, args);
    }
}
