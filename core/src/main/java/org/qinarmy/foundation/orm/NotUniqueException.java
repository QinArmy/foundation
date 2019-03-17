package org.qinarmy.foundation.orm;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.tx.ArmyDataAccessException;

/**
 * 使用唯一键查询查出多个结果时抛出.
 * created  on 2019-03-17.
 */
public class NotUniqueException extends ArmyDataAccessException {

    public NotUniqueException() {
        super(ResultCode.NO_UNIQUE,ResultCode.NO_UNIQUE.display());
    }

    public NotUniqueException(String format, Object... args) {
        super(ResultCode.NO_UNIQUE, format, args);
    }

    public NotUniqueException(Throwable cause, String format, Object... args) {
        super(ResultCode.NO_UNIQUE, cause, format, args);
    }
}
