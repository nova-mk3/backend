package org.nova.backend.auth.application.dto.request;

import lombok.Getter;

@Getter
public class SignUpRequest {

    private MemberSignUpRequest memberSignUpRequest;

    private GraduationSignUpRequest graduationSignUpRequest;

}
