package org.qinarmy.foundation.tx;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.qinarmy.foundation.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * @see TransactionDefinitionHolder
 * @see TransactionInterceptor
 * @see Transactional
 * created  on 2018/6/24.
 */
public class TransactionDefinitionInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionDefinitionInterceptor.class);


    private enum DefType {
        INNER,
        OUTER,
        NONE,
        NOT_SUPPORTED,
        ERROR
    }

    private TransactionAttributeSource transactionAttributeSource;

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        transactionAttributeSource = applicationContext.getBean(TransactionAttributeSource.class);
        Assert.notNull(transactionAttributeSource, "config error");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        if (targetClass == null) {
            throw new IllegalStateException(
                    String.format("事务定义 aop 配置错误,class[%s],method[%s]", targetClass, invocation.getMethod()));
        }
        // 获取目标方法 @Transactional
        final TransactionAttribute definition = transactionAttributeSource.getTransactionAttribute(
                invocation.getMethod(), targetClass);

        if (definition == null) {
            throw new IllegalStateException(String.format("事务定义 aop 配置错误,method[%s]", invocation.getMethod()));
        }

        DefType defType;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            defType = decideDefTypeWithTx(invocation, definition);
        } else {
            defType = decideDefTypeWithoutTx(invocation, definition);
        }

        Throwable e = null;
        Object result = null;
        try {
            // 调用下层 AOP, 若有.
            result = invocation.proceed();

        } catch (Throwable throwable) {
            e = throwable;
        } finally {
            doAfterProceed(invocation, defType, e);
        }

        if (e != null) {
            // 返回前抛出异常
            throw e;
        }

        return result;
    }

    private void doAfterProceed(MethodInvocation invocation, DefType defType, Throwable e) {
        switch (defType) {
            case OUTER:
                TransactionDefinitionHolder.pop();
                LOG.debug("外部事务结束: {}", invocation.getMethod());
                break;
            case INNER:
                LOG.debug("内部事务结束: {}", invocation.getMethod());
                break;
            case NONE:
                LOG.debug("无事务方法结束:{}", invocation.getMethod());
                break;
            case NOT_SUPPORTED:
                TransactionDefinitionHolder.pop();
                LOG.debug("NOT_SUPPORTED 事务结束: {}", invocation.getMethod());
                break;
            case ERROR:
                LOG.debug("事务抛出异常:{},{}", e != null, invocation.getMethod());
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown DefType[%s]", defType));
        }
    }

    /**
     * 当前有事务的环境下决定事务定义类型
     */
    private DefType decideDefTypeWithTx(MethodInvocation invocation, TransactionAttribute definition) {
        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_SUPPORTS:
            case TransactionDefinition.PROPAGATION_MANDATORY:
            case TransactionDefinition.PROPAGATION_NESTED:
                LOG.debug("内部事务开始: {}", invocation.getMethod());
                defType = DefType.INNER;
                break;
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.OUTER;
                LOG.debug("新外部事务开始: {}", invocation.getMethod());
                break;
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.NOT_SUPPORTED;
                LOG.debug("事务将被挡挂起: {}", TransactionDefinitionHolder.getName());
                break;
            case TransactionDefinition.PROPAGATION_NEVER:
                defType = DefType.ERROR;
                LOG.debug("事务将抛出异常: {}", TransactionDefinitionHolder.getName());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("未知事务传播行为,%s", definition.getPropagationBehavior()));

        }
        return defType;
    }

    /**
     * 当前没有事务的环境下决定事务定义类型
     */
    private DefType decideDefTypeWithoutTx(MethodInvocation invocation, TransactionAttribute definition) {
        DefType defType;
        switch (definition.getPropagationBehavior()) {
            case TransactionDefinition.PROPAGATION_REQUIRED:
            case TransactionDefinition.PROPAGATION_REQUIRES_NEW:
            case TransactionDefinition.PROPAGATION_NESTED:
                // push 外层事务定义
                TransactionDefinitionHolder.push(definition, invocation.getMethod());
                defType = DefType.OUTER;
                LOG.debug("外部事务开始: {}", invocation.getMethod());
                break;
            case TransactionDefinition.PROPAGATION_NOT_SUPPORTED:
            case TransactionDefinition.PROPAGATION_SUPPORTS:
            case TransactionDefinition.PROPAGATION_NEVER:
                defType = DefType.NONE;
                LOG.debug("无事务执行: {}", invocation.getMethod());
                break;
            case TransactionDefinition.PROPAGATION_MANDATORY:
                defType = DefType.ERROR;
                LOG.debug("事务将抛出异常: {}", TransactionDefinitionHolder.getName());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("未知事务传播行为,%s", definition.getPropagationBehavior()));

        }
        return defType;
    }

}
