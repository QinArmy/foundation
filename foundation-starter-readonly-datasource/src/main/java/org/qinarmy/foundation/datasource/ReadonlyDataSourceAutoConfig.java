package org.qinarmy.foundation.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.qinarmy.foundation.tx.TransactionDefinitionInterceptor;
import org.qinarmy.foundation.tx.TransactionInterceptorAutoConfig;
import org.qinarmy.foundation.util.StringUtils;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Description;
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
    @RefreshScope
    public DruidDataSource coreSecondaryDataSource() {
        return createDataSource(env, CORE, DataSourceRole.SECONDARY);
    }

    @Bean(value = TIMEOUT_SECONDARY, initMethod = "init", destroyMethod = "close")
    @Description("也是从库,只是查询的超时时间特别长")
    @RefreshScope
    public DruidDataSource coreTimeoutSecondaryDataSource() {
        return createDataSource(env, CORE, DataSourceRole.TIMEOUT);
    }

    @Bean
    @DependsOn({SECONDARY, TIMEOUT_SECONDARY})
    public PrimarySecondaryRoutingDataSource coreRoutingDataSource(@Qualifier(SECONDARY) DruidDataSource secondary,
                                                                   @Qualifier(TIMEOUT_SECONDARY) DruidDataSource timeoutSecondary) {

        PrimarySecondaryRoutingDataSource dataSource = new PrimarySecondaryRoutingDataSource();

        dataSource.setDefaultTargetDataSource(secondary);

        Map<String, Object> dataSourceMap = new HashMap<>(6);
        DruidDataSource primaryDataSource = applicationContext.getBean(DruidDataSource.class, PRIMARY);
        if (primaryDataSource != null) {
            dataSourceMap.put(PrimarySecondaryRoutingDataSource.PRIMARY, primaryDataSource);
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

    /**
     * 封装 数据源创建逻辑
     */
    public static DruidDataSource createDataSource(Environment env, final String tag, DataSourceRole role) {
        DruidDataSource ds = new DruidDataSource();

        ds.setUrl(env.getRequiredProperty(String.format("spring.datasource.%s.%s.url", tag, role)));
        ds.setUsername(env.getRequiredProperty(String.format("spring.datasource.%s.%s.username", tag, role)));
        ds.setPassword(env.getRequiredProperty(String.format("spring.datasource.%s.%s.password", tag, role)));
        ds.setDriverClassName(getDriver(env, tag));

        ds.setInitialSize(env.getProperty(String.format("spring.datasource.%s.%s.initialSize", tag, role), Integer.class, 10));
        ds.setMaxActive(env.getProperty(String.format("spring.datasource.%s.%s.maxActive", tag, role), Integer.class, 200));
        ds.setMaxWait(env.getProperty(String.format("spring.datasource.%s.%s.maxWait", tag, role), Long.class, 27L * 1000L));
        ds.setValidationQuery(env.getProperty(String.format("spring.datasource.%s.%s.validationQuery", tag, role), "SELECT NOW() FROM dual"));

        ds.setTestOnBorrow(env.getProperty(String.format("spring.datasource.%s.%s.testOnBorrow", tag, role), Boolean.class, Boolean.FALSE));
        ds.setTestWhileIdle(env.getProperty(String.format("spring.datasource.%s.%s.testWhileIdle", tag, role), Boolean.class, Boolean.TRUE));
        ds.setTestOnReturn(env.getProperty(String.format("spring.datasource.%s.%s.testOnReturn", tag, role), Boolean.class, Boolean.FALSE));
        ds.setTimeBetweenEvictionRunsMillis(env.getProperty(String.format("spring.datasource.%s.%s.timeBetweenEvictionRunsMillis", tag, role), Long.class, 5L * 1000L));

        ds.setRemoveAbandoned(env.getProperty(String.format("spring.datasource.%s.%s.removeAbandoned", tag, role), Boolean.class, Boolean.FALSE));
        ds.setMinEvictableIdleTimeMillis(env.getProperty(String.format("spring.datasource.%s.%s.minEvictableIdleTimeMillis", tag, role), Long.class, 30000L));
        return ds;
    }

    private static String getDriver(Environment env, String tag) {
        String driver;
        driver = env.getProperty(String.format("spring.datasource.%s.driver-class-name", tag));
        if (!StringUtils.hasText(driver)) {
            driver = env.getRequiredProperty("spring.datasource.driver-class-name");
        }
        return driver;
    }


}
