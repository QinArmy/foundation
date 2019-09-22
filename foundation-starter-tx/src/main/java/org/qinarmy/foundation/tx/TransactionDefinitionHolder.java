package org.qinarmy.foundation.tx;

import org.springframework.core.NamedThreadLocal;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 事务定义 holder
 *
 * @see TransactionDefinitionInterceptor
 * @see AbstractRoutingDataSource
 * created  on 2018/6/24.
 */
public abstract class TransactionDefinitionHolder {

    private static final ThreadLocal<TransactionAttribute> HOLDER = new NamedThreadLocal<>("事务定义");

    private static final ThreadLocal<String> TRANSACTION_NAME_HOLDER =
            new NamedThreadLocal<>("事务名holder");

    private static final Map<Integer, Isolation> ISOLATION_MAP = initIsolationMap();

    private static final Map<Integer, Propagation> PROPAGATION_MAP = initPropagationMap();


    private static Map<Integer, Isolation> initIsolationMap() {
        Map<Integer, Isolation> map = new HashMap<>(10);
        for (Isolation value : Isolation.values()) {
            map.put(value.value(), value);
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map<Integer, Propagation> initPropagationMap() {
        Map<Integer, Propagation> map = new HashMap<>(10);
        for (Propagation value : Propagation.values()) {
            map.put(value.value(), value);
        }
        return Collections.unmodifiableMap(map);
    }


    static void set(@NonNull TransactionAttribute transactionDefinition, @NonNull Method method) {
        if (transactionDefinition == null) {
            clear();
        } else {
            HOLDER.set(transactionDefinition);
            TRANSACTION_NAME_HOLDER.set(method.toString());
        }
    }

    public static TransactionAttribute get() {
        return HOLDER.get();
    }

    public static boolean isReadOnly() {
        TransactionDefinition transactionDefinition = get();
        return transactionDefinition == null || transactionDefinition.isReadOnly();
    }

    /**
     * 事务隔离级别
     *
     * @return null or {@link Isolation}
     */
    @Nullable
    public static Isolation getIsolation() {
        TransactionDefinition transactionDefinition = get();
        Isolation level;
        if (transactionDefinition != null) {
            level = ISOLATION_MAP.get(transactionDefinition.getIsolationLevel());
        } else {
            level = null;
        }
        return level;
    }

    /**
     * @return null or {@link Propagation}
     */
    @Nullable
    public static Propagation getPropagation() {
        TransactionDefinition transactionDefinition = get();
        Propagation propagation;
        if (transactionDefinition != null) {
            propagation = PROPAGATION_MAP.get(transactionDefinition.getPropagationBehavior());
        } else {
            propagation = null;
        }
        return propagation;
    }


    public static int getTimeout() {
        TransactionDefinition transactionDefinition = get();
        return transactionDefinition == null
                ? TransactionDefinition.TIMEOUT_DEFAULT
                : transactionDefinition.getTimeout();
    }

    /**
     * 获取事务名称 or null
     */
    public static String getName() {
        TransactionDefinition transactionDefinition = get();
        String name = null;
        if (transactionDefinition != null) {
            name = transactionDefinition.getName();
            if (name == null) {
                name = TRANSACTION_NAME_HOLDER.get();
            }
        }
        return name;

    }

    static void clear() {
        HOLDER.remove();
    }


}
