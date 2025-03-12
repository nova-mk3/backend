package org.nova.backend.email.application.mapper;

import java.util.UUID;
import org.nova.backend.email.domain.model.EmailAuth;
import org.springframework.stereotype.Component;

@Component
public class EmailAuthMapper {

    public EmailAuth toEntity(String email, String code) {
        return new EmailAuth(
                UUID.randomUUID(),
                email,
                code
        );
    }
}
