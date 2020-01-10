package org.qinarmy.foundation.core.condition;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;


class OnPrimaryDataSourceCondition extends SpringBootCondition implements ConfigurationCondition {

    private static final String HAS_PRIMARY = "hasPrimary";

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage conditionMessage = ConditionMessage.empty();
        conditionMessage.andCondition(ConditionalOnPrimaryDataSource.class);
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        if (factory == null) {
            return ConditionOutcome.noMatch(ConditionMessage
                    .forCondition(ConditionalOnPrimaryDataSource.class)
                    .because("ConfigurableListableBeanFactory is null"));
        }
        Map<String, AbstractRoutingDataSource> beanMap;
        beanMap = factory.getBeansOfType(AbstractRoutingDataSource.class);

        for (Map.Entry<String, AbstractRoutingDataSource> entry : beanMap.entrySet()) {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entry.getValue());
            if (!beanWrapper.isReadableProperty(HAS_PRIMARY)) {
                continue;
            }
            if (Boolean.TRUE.equals(beanWrapper.getPropertyValue(HAS_PRIMARY))) {
                return ConditionOutcome.match(
                        ConditionMessage.forCondition(ConditionalOnPrimaryDataSource.class)
                                .found("primary data source").items(entry.getKey())
                );
            }
        }

        return ConditionOutcome.noMatch(ConditionMessage
                .forCondition(ConditionalOnPrimaryDataSource.class)
                .because("not found primary datasource"))
                ;
    }
}
