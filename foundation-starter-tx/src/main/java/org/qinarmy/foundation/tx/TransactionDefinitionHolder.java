package org.qinarmy.foundation.tx;

import org.qinarmy.foundation.util.Assert;
import org.qinarmy.foundation.util.StringUtils;
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

    private static final ThreadLocal<TxDefinitionHolder> HOLDER = new NamedThreadLocal<>("事务定义holder");

    private static final Map<Integer, Isolation> ISOLATION_MAP = initIsolationMap();

    private static final Map<Integer, Propagation> PROPAGATION_MAP = initPropagationMap();


    /**
     * 用以封闭事务定义形成链表结构.
     */
    private static class TxDefinitionHolder {

        /**
         * 被挂起的事务定义,相当于链表中的 previous .
         */
        @Nullable
        private final TxDefinitionHolder suspended;

        /**
         * 当前最外层事务定义.
         */
        @NonNull
        private final TransactionAttribute definition;

        /**
         * 事务名或方法名
         */
        @NonNull
        private final String name;

        public TxDefinitionHolder(@Nullable TxDefinitionHolder suspended, @NonNull TransactionAttribute definition,
                                  @NonNull String name) {
            this.suspended = suspended;
            this.definition = definition;
            this.name = name;
            Assert.notNull(definition, "definition required");
        }
    }


    static void push(@NonNull TransactionAttribute definition, @NonNull Method method) {
        String txName = StringUtils.hasText(definition.getName()) ? definition.getName() : method.toString();
        HOLDER.set(new TxDefinitionHolder(HOLDER.get(), definition, txName));
    }

    static void pop() {
        TxDefinitionHolder current = HOLDER.get();
        if (current != null) {
            if (current.suspended == null) {
                HOLDER.remove();
            } else {
                HOLDER.set(current.suspended);
            }
        } else {
            throw new IllegalStateException("current tx definition holder error.");
        }
    }


    public static TransactionAttribute get() {
        TxDefinitionHolder holder = HOLDER.get();

        TransactionAttribute attribute = null;
        if (holder != null
                && isOuterDef(holder.definition.getPropagationBehavior())) {
            attribute = holder.definition;

        }
        return attribute;
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
    @Nullable
    public static String getName() {
        TransactionDefinition transactionDefinition = get();
        String name = null;
        if (transactionDefinition != null) {
            name = transactionDefinition.getName();
            if (name == null) {
                name = HOLDER.get().name;
            }
        }
        return name;
    }

    /*################################## blow private method ##################################*/

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

    /**
     * 注意 {@link TransactionDefinition#PROPAGATION_NOT_SUPPORTED} 虽可被 push 到 holder 中,
     * 但它在本类中不是外层事务定义,因为它以无事务执行.
     *
     * @return true 传播行为是外层事务定义
     */
    private static boolean isOuterDef(int def) {
        return def == TransactionDefinition.PROPAGATION_REQUIRED
                || def == TransactionDefinition.PROPAGATION_REQUIRES_NEW
                || def == TransactionDefinition.PROPAGATION_NESTED;
    }


}
