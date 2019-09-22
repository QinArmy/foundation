package org.qinarmy.foundation.tx;


import org.qinarmy.foundation.core.ResultCode;
import org.qinarmy.foundation.core.RuntimeBusinessException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.EnumSet;
import java.util.Set;

/**
 * 事务工具,用于帮助保证事务环境.
 * created  on 2019-03-08.
 *
 * @see TransactionDefinitionHolder
 */
public abstract class TransactionalUtils {

    /**
     * 为保证数据安全的一般性事务级别,如:创建数据,对数据的不包含金额的修改.
     */
    private static final Set<Isolation> GENERAL_ISOLATION = EnumSet.of(
            Isolation.READ_COMMITTED,
            Isolation.REPEATABLE_READ,
            Isolation.SERIALIZABLE
    );

    /**
     * 为保证数据安全的一金额变动事务级别,如:账户的资金变动
     */
    private static final Set<Isolation> AMOUNT_ISOLATION = EnumSet.of(
            Isolation.REPEATABLE_READ,
            Isolation.SERIALIZABLE
    );


    /**
     * @return true 是一般性事务.
     * @see TransactionDefinitionHolder#getIsolation()
     */
    public static boolean isGeneralIsolation() {
        return GENERAL_ISOLATION.contains(TransactionDefinitionHolder.getIsolation());
    }

    /**
     * @return true 是金额变动事务.
     * @see TransactionDefinitionHolder#getIsolation()
     */
    public static boolean isAmountIsolation() {
        return AMOUNT_ISOLATION.contains(TransactionDefinitionHolder.getIsolation());
    }

    /**
     * 断言当前事务是 金额级事务
     *
     * @throws ArmyDataAccessException 当前事务不是金额级事务
     * @see #AMOUNT_ISOLATION
     */
    public static void assertAmountIsolation(@Nullable String message) throws ArmyDataAccessException {
        if (!isAmountIsolation()) {
            throw new ArmyDataAccessException(ResultCode.ISOLATION,
                    message != null ? message : "当前事务隔离级别不是金额级");
        }
    }

    /**
     * 断言当前事务是 一般级
     *
     * @throws ArmyDataAccessException 当前事务不是表一般级事务
     * @see #GENERAL_ISOLATION
     */
    public static void assertGeneralIsolation(@Nullable String message) throws ArmyDataAccessException {
        if (!isGeneralIsolation()) {
            throw new ArmyDataAccessException(ResultCode.ISOLATION,
                    message != null ? message : "当前事务隔离级别不是一般级");
        }
    }


    /**
     * 断言无事务环境
     */
    public static void assertNoTransaction() throws RuntimeBusinessException {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeBusinessException(ResultCode.NEVER_TRANSACTIONAL, "当前环境有事务");
        }

    }


}
