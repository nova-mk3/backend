package org.nova.backend.member.application.mapper;

import java.util.UUID;
import org.nova.backend.auth.application.dto.request.GraduationSignUpRequest;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.springframework.stereotype.Component;

@Component
public class GraduationMapper {

    public Graduation toEntity(GraduationSignUpRequest request) {
        return new Graduation(
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
