package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.member.application.dto.request.UpdateGraduationRequest;
import org.nova.backend.member.application.dto.response.GraduationResponse;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.springframework.stereotype.Component;

@Component
public class GraduationMapper {

    public Graduation toEntity(PendingGraduation pendingGraduation) {
        return new Graduation(
                UUID.randomUUID(),
                pendingGraduation.getYear(),
                pendingGraduation.isContact(),
                pendingGraduation.isWork(),
                pendingGraduation.getJob(),
                pendingGraduation.getContactInfo(),
                pendingGraduation.getContactDescription()
        );
    }

    public Graduation toEntity(int year, UpdateGraduationRequest graduationRequest) {
        return new Graduation(
                UUID.randomUUID(),
                year,
                graduationRequest.isContact(),
                graduationRequest.isWork(),
                graduationRequest.getJob(),
                graduationRequest.getContactInfo(),
                graduationRequest.getContactDescription()
        );
    }

    public GraduationResponse toResponse(Graduation graduation) {
        if (graduation == null) {
            return toBlankResponse();
        } else {
            return new GraduationResponse(
                    graduation.getId(),
                    graduation.getYear() + "년",
                    graduation.isContact(),
                    graduation.isWork(),
                    graduation.getJob(),
                    graduation.getContactInfo(),
                    graduation.getContactDescription()
            );
        }
    }

    private GraduationResponse toBlankResponse() {
        return new GraduationResponse(
                null,
                null,
                false,
                false,
                "",
                "",
                ""
        );
    }

}
