package com.projectmanagement.infrastructure.adapter.output.audit;

import com.projectmanagement.domain.port.output.AuditLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggerAuditAdapter implements AuditLogPort {

    private static final Logger logger = LoggerFactory.getLogger(LoggerAuditAdapter.class);

    @Override
    public void register(String action, UUID entityId) {
        logger.info("AUDIT: Action={}, EntityId={}", action, entityId);
    }
}
