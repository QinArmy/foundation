package org.qinarmy.foundation.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationFailListener implements ApplicationListener<ApplicationFailedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFailListener.class);

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        LOG.error("application start up failure:", event.getException());
    }
}
