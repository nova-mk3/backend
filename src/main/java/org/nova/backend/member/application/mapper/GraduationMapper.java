package org.nova.backend.member.application.mapper;

import java.util.UUID;
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

}
