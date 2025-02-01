package org.nova.backend.member.application.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingMemberListResponse {

    private long totalPendingMemberCount;

    private List<PendingMemberResponse> pendingMemberResponseList;

}
