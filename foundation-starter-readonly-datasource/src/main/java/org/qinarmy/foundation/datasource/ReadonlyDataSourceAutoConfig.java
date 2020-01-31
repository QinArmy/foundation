package org.qinarmy.foundation.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.qinarmy.foundation.tx.TransactionDefinitionInterceptor;
import org.qinarmy.foundation.tx.TransactionInterceptorAutoConfig;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConditionalOnClass(value = {TransactionDefinitionInterceptor.class, DefaultPointcutAdvisor.class})
@AutoConfigureAfter(value = {TransactionInterceptorAutoConfig.class})
@Configuration
public class ReadonlyDataSourceAutoConfig implements EnvironmentAware, ApplicationContextAware, ApplicationListener<EnvironmentChangeEvent> {

    static final String CORE = "core";


    static final String PRIMARY = "corePrimaryDataSource";

    private static final String SECONDARY = "coreSecondaryDataSource";

    private static final String TIMEOUT_SECONDARY = "coreTimeoutDataSource";

    private Environment env;

    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean(value = SECONDARY, initMethod = "init", destroyMethod = "close")
    //@RefreshScope
    public DruidDataSource coreSecondaryDataSource() {
        return DataSourceUtils.createDataSource(env, CORE, DataSourceRole.SECONDARY);
    }

    @Bean(value = TIMEOUT_SECONDARY, initMethod = "init", destroyMethod = "close")
    @Description("也是从库,只是查询的超时时间特别长")
    //@RefreshScope
    public DruidDataSource coreTimeoutSecondaryDataSource() {
        return DataSourceUtils.createDataSource(env, CORE, DataSourceRole.TIMEOUT);
    }

    @Bean
    @DependsOn({SECONDARY, TIMEOUT_SECONDARY})
    @Primary
    public PrimarySecondaryRoutingDataSource coreRoutingDataSource(@Qualifier(SECONDARY) DruidDataSource secondary,
                                                                   @Qualifier(TIMEOUT_SECONDARY) DruidDataSource timeoutSecondary) {
        PrimarySecondaryRoutingDataSource dataSource = new PrimarySecondaryRoutingDataSource();

        dataSource.setDefaultTargetDataSource(secondary);

        Map<String, Object> dataSourceMap = new HashMap<>(6);
        if (applicationContext.containsBean(PRIMARY)) {
            dataSourceMap.put(PrimarySecondaryRoutingDataSource.PRIMARY,
                    applicationContext.getBean(DruidDataSource.class, PRIMARY));
        }

        dataSourceMap.put(PrimarySecondaryRoutingDataSource.SECONDARY, secondary);
        dataSourceMap.put(PrimarySecondaryRoutingDataSource.TIMEOUT_SECONDARY, timeoutSecondary);

        dataSource.setTargetDataSources(Collections.unmodifiableMap(dataSourceMap));

        dataSource.setTimeoutBoundary
                (env.getProperty(String.format("spring.datasource.%s.timeout.boundary", "core"), Integer.class, 10));
        return dataSource;
    }

    /**
     * 当数据源配置改变时,刷新数据源
     */
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope = null;

        Set<String> changeKeySet = event.getKeys();
        String beanName;
        for (String key : changeKeySet) {
            beanName = parseDataSourceBeanName(key);
            if (beanName == null) {
                continue;
            }
            if (!isRefreshableDataSourceBean(beanName, applicationContext)) {
                continue;
            }
            if (refreshScope == null) {
                refreshScope = applicationContext.getBean(
                        org.springframework.cloud.context.scope.refresh.RefreshScope.class);
            }
            // 配置改变,刷新数据源
            refreshScope.refresh(beanName);
        }
    }

    private static boolean isRefreshableDataSourceBean(String beanName, ApplicationContext applicationContext) {
        return applicationContext.containsBean(beanName)
                && applicationContext.isTypeMatch(beanName, DruidDataSource.class)
                && applicationContext.findAnnotationOnBean(beanName, RefreshScope.class) != null
                ;
    }

    /**
     * @param key start with {@code spring.datasource.}
     * @return eg: spring.datasource.core.primary.url > corePrimaryDataSource
     */
    @Nullable
    private static String parseDataSourceBeanName(String key) {
        String prefix = "spring.datasource.";
        if (!key.startsWith(prefix)) {
            return null;
        }
        int tagIndex = prefix.length();
        if (tagIndex >= key.length()) {
            return null;
        }
        int roleIndex = key.indexOf('.', tagIndex);
        if (roleIndex < 0) {
            return null;
        }

        int optionIndex = key.indexOf('.', roleIndex + 1);
        if (optionIndex < 0) {
            return null;
        }

        String tag = key.substring(tagIndex, roleIndex);
        String role = key.substring(roleIndex + 1, optionIndex);

        if (role.length() < 2) {
            return null;
        }
        return tag + Character.toUpperCase(role.charAt(0)) + role.substring(1) + "DataSource";
    }


}
