package org.nova.backend.member.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageMemberResponse {

    private boolean isLoginMember;

    private MemberResponse memberResponse;

}
