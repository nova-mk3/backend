package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.auth.application.dto.request.GraduationSignUpRequest;
import org.nova.backend.member.application.dto.response.PendingGraduationResponse;
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

    public PendingGraduationResponse toResponse(PendingGraduation pendingGraduation) {
        if (pendingGraduation == null) {
            return toBlankResponse();
        }
        return toInfoResponse(pendingGraduation);
    }

    private PendingGraduationResponse toBlankResponse() {
        return new PendingGraduationResponse(
                null,
                0,
                false,
                false,
                null,
                null,
                null
        );
    }

    private PendingGraduationResponse toInfoResponse(PendingGraduation pendingGraduation) {
        return new PendingGraduationResponse(
                pendingGraduation.getId(),
                pendingGraduation.getYear(),
                pendingGraduation.isContact(),
                pendingGraduation.isWork(),
                pendingGraduation.getJob(),
                pendingGraduation.getContactInfo(),
                pendingGraduation.getContactDescription()
        );
    }

}
