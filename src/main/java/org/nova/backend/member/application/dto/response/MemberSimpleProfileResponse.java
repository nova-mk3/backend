package org.nova.backend.member.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSimpleProfileResponse {

    private UUID memberId;
    private String name;
    private ProfilePhotoResponse profilePhoto;

}
