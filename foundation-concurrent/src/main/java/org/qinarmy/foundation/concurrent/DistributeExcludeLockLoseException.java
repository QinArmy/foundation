package org.qinarmy.foundation.concurrent;

import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;

/**
 * 当分布式排他锁丢失时抛出 可选择性抛出.
 * @see DistributeLock
 * created  on 2018/9/14.
 */
public class DistributeExcludeLockLoseException extends RuntimeBusinessException {


    private static final long serialVersionUID = -2427515924477724393L;

    public DistributeExcludeLockLoseException( String format, Object... args) {
        super(ResultCode.DISTRIBUTE_LOCK, format, args);
    }

    public DistributeExcludeLockLoseException( Throwable cause, String format, Object... args) {
        super(ResultCode.DISTRIBUTE_LOCK, cause, format, args);
    }
}
