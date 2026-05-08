package org.nova.backend.member.application.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class UpdateMemberRequest {

    @Valid
    private UpdateMemberProfileRequest updateMemberProfileRequest;

    @Valid
    private UpdateGraduationRequest updateGraduationRequest;

}
