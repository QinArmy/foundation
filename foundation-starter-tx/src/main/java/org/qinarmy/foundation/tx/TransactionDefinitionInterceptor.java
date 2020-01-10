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
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * @see TransactionDefinitionHolder
 * created  on 2018/6/24.
 */
public class TransactionDefinitionInterceptor implements MethodInterceptor, InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger( TransactionDefinitionInterceptor.class );

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
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass( invocation.getThis() ) : null);

        if (targetClass != null && !TransactionSynchronizationManager.isActualTransactionActive()) {
            LOG.debug( "外层事务:开始{},{}", invocation.getMethod(), targetClass );
            TransactionAttribute definition = transactionAttributeSource.getTransactionAttribute(
                    invocation.getMethod(), targetClass);

            if (definition != null && definition.getPropagationBehavior()
                    != TransactionDefinition.PROPAGATION_NEVER) {
                TransactionDefinitionHolder.set(definition, invocation.getMethod());
            }
        } else {
            LOG.debug( "内层事务:开始{},{}", invocation.getMethod(), targetClass );
        }

        Throwable e = null;
        Object result = null;
        try {

            result = invocation.proceed();

        } catch (Throwable throwable) {
            e = throwable;
        } finally {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                LOG.debug( "外层事务结束:{},{}", invocation.getMethod(), targetClass );
                // 事务结束
                TransactionDefinitionHolder.clear();
            } else {
                LOG.debug( "内层事务:线束{},{}", invocation.getMethod(), targetClass );
            }

        }


        if (e != null) {
            throw e;
        }

        return result;
    }


}
