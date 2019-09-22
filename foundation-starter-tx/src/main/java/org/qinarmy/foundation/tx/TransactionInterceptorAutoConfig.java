package org.qinarmy.foundation.tx;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

/**
 * created  on 2019-03-17.
 */
@Configuration
@ConditionalOnBean(value = PlatformTransactionManager.class)
public class TransactionInterceptorAutoConfig {

    @Bean
    @Description("用于在spring 事务增强之前获取事务定义,以便实现读写分离.")
    public TransactionDefinitionInterceptor transactionDefinitionInterceptor() {
        return new TransactionDefinitionInterceptor();
    }


    /**
     * @see #transactionDefinitionInterceptor()
     */
    @Bean
    public DefaultPointcutAdvisor transactionDefinitionPointcutAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.format( "@annotation(%s)", Transactional.class.getName()) );

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor( pointcut, transactionDefinitionInterceptor() );
        advisor.setOrder( 0 );
        return advisor;
    }


}
