package com.projectmanagement.infrastructure.adapter.output.notification;

import com.projectmanagement.domain.port.output.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(LoggerNotificationAdapter.class);

    @Override
    public void notify(String message) {
        logger.info("NOTIFICATION: Message={}", message);
    }
}
