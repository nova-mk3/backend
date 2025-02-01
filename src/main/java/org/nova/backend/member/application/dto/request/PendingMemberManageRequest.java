package org.nova.backend.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PendingMemberManageRequest {

    @NotBlank
    private UUID pendingMemberId;

}
