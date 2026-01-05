package com.projectmanagement.domain.port.output;

import com.projectmanagement.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface CurrentUserPort {
    Optional<UUID> getCurrentUserId();
}
