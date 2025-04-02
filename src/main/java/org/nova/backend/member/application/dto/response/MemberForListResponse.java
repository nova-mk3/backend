package org.nova.backend.member.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberForListResponse {

    private String studentNumber;
    private String name;
    private String email;
    private boolean isGraduation;
    private String grade;
    private boolean isAbsence;
    private ProfilePhotoResponse profilePhoto;
    private String phone;

}
