package org.nova.backend.member.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nova.backend.member.domain.model.valueobject.Role;

@Getter
@AllArgsConstructor
public class ExecutiveHistoryResponse {

    private UUID executiveHistoryId;
    private int year;
    private Role role;
    private String name;

    private UUID memberId;
    private String studentNumber;
    private ProfilePhotoResponse profilePhoto;
    private String phone;
    private String grade;
    private String semester;
    private boolean isGraduation;
    private Integer graduationYear;
}
