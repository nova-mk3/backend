package org.nova.backend.auth.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nova.backend.member.domain.model.valueobject.Role;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private UUID memberId;
    private String studentNumber;
    private String name;
    private String email;
    private boolean isGraduation;
    private int year;
    private int semester;
    private boolean isAbsence;
    private String profilePhoto;
    private String phone;
    private String introduction;
    private String birth;
    private Role role;

}
