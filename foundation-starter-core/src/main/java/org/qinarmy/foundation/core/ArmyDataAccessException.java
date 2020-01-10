package org.qinarmy.foundation.core;

/**
 * created  on 2019-03-13.
 * org.springframework.dao.DataAccessException
 */
public class ArmyDataAccessException extends RuntimeBusinessException {


    private static final long serialVersionUID = -2262308718840431553L;

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
