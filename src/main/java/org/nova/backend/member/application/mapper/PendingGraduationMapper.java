package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.auth.application.dto.request.GraduationSignUpRequest;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.springframework.stereotype.Component;

@Component
public class PendingGraduationMapper {

    public PendingGraduation toEntity(GraduationSignUpRequest request) {
        return new PendingGraduation(
                UUID.randomUUID(),
                request.getYear(),
                request.isContact(),
                request.isWork(),
                request.getJob(),
                request.getContactInfo(),
                request.getContactDescription()
        );
    }

}
