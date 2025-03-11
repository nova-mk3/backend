package org.nova.backend.member.application.dto.request;

import lombok.Getter;

@Getter
public class UpdateMemberRequest {

    private UpdateMemberProfileRequest updateMemberProfileRequest;

    private UpdateGraduationRequest updateGraduationRequest;

}
