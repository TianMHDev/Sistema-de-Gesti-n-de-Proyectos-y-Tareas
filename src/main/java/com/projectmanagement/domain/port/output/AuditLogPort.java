package com.projectmanagement.domain.port.output;

import java.util.UUID;

public interface AuditLogPort {
    void register(String action, UUID entityId);
}
