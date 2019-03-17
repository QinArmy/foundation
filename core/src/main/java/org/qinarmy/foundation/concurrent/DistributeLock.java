package org.qinarmy.foundation.concurrent;

/**
 * 分布式锁
 * created  on 2018/9/8.
 */
public interface DistributeLock {


    /**
     * 尝试获取分布式锁
     *
     * @return true 获取锁成功
     * @see LockOption
     */
    boolean tryLock(LockOption option);

    /**
     * 尝试获取锁,若失败则抛出异常
     */
    default void tryLockWithException(LockOption option)throws DistributeExcludeLockLoseException{
        if(!tryLock( option )){
            throw new DistributeExcludeLockLoseException(
                    "holder[%s] fail to acquire lock[%s]",option.holder,option.key );
        }
    }


    /**
     * 释放分布式锁
     *
     * @return true 释放锁成功,当 holder 不是锁持有都时 返回 false
     * @see LockOption
     */
    boolean releaseLock(LockOption option);


}
