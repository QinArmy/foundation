package org.qinarmy.foundation.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@AutoConfigureBefore(value = {ReadonlyDataSourceAutoConfig.class})
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(value = ReadonlyDataSourceAutoConfig.PRIMARY, initMethod = "init", destroyMethod = "close")
    @RefreshScope
    public DruidDataSource corePrimaryDataSource() {
        return ReadonlyDataSourceAutoConfig.createDataSource(
                env, ReadonlyDataSourceAutoConfig.CORE, DataSourceRole.PRIMARY);
    }


}
