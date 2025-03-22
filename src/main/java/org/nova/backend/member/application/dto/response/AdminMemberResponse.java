package org.nova.backend.member.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminMemberResponse {

    private MemberResponse memberResponse;
    private GraduationResponse graduationResponse;

}
