package org.qinarmy.foundation.orm;

import org.qinarmy.foundation.core.ArmyDataAccessException;
import org.qinarmy.foundation.core.ResultCode;

/**
 * 使用唯一键查询查出多个结果时抛出.
 * created  on 2019-03-17.
 */
public class NonUniqueException extends ArmyDataAccessException {

    private static final long serialVersionUID = -8582311350078823156L;

    public NonUniqueException() {
        super(ResultCode.NO_UNIQUE, ResultCode.NO_UNIQUE.display());
    }

    public NonUniqueException(String format, Object... args) {
        super(ResultCode.NO_UNIQUE, format, args);
    }

    public NonUniqueException(Throwable cause, String format, Object... args) {
        super(ResultCode.NO_UNIQUE, cause, format, args);
    }
}
