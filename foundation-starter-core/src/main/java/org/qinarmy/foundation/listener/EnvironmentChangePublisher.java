package org.qinarmy.foundation.listener;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.qinarmy.foundation.util.ClassUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

public class EnvironmentChangePublisher implements ApplicationListener<ApplicationReadyEvent> {

    private static final boolean CTRIP_APOLLO_IS_PRESENT = ClassUtils.isPresent("com.ctrip.framework.apollo.Config",
            EnvironmentChangePublisher.class.getClassLoader());

    private static final boolean CLOUD_IS_PRESENT = ClassUtils.isPresent(
            "org.springframework.cloud.context.environment.EnvironmentChangeEvent",
            EnvironmentChangePublisher.class.getClassLoader());


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();

        Environment env = context.getEnvironment();
        if (CLOUD_IS_PRESENT && CTRIP_APOLLO_IS_PRESENT) {
            String[] namespaces = env.getRequiredProperty("apollo.bootstrap.namespaces", String[].class);
            for (String namespace : namespaces) {
                Config config = ConfigService.getConfig(namespace);
                config.addChangeListener(apolloEvent ->
                        context.publishEvent(new EnvironmentChangeEvent(context, apolloEvent.changedKeys())));
            }
        }
    }


}
