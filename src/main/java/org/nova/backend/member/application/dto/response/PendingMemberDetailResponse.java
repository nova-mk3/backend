package org.nova.backend.member.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingMemberDetailResponse {

    private PendingMemberResponse pendingMemberResponse;

    private PendingGraduationResponse pendingGraduationResponse;

}
