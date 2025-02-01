package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.request.PendingMemberManageRequest;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.response.PendingMemberDetailResponse;
import org.nova.backend.member.application.dto.response.PendingMemberListResponse;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.application.service.PendingMemberService;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입 요청 처리 API", description = "관리자가 회원가입 요청을 처리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pendingMembers")
public class PendingMemberController {

    private final PendingMemberService pendingMemberService;
    private final MemberMapper memberMapper;

    /**
     * 모든 회원가입 요청 리스트
     *
     * @return 회원가입 요청 리스트
     */
    @GetMapping("")
    @PendingMemberApiDocument.GetPendingMemberListApiDoc
    public ApiResponse<PendingMemberListResponse> getPendingMemberList() {
        long totalPendingMemberCount = pendingMemberService.getPendingMemberCount();
        List<PendingMemberResponse> pendingMemberResponseList = pendingMemberService.getPendingMemberList();

        PendingMemberListResponse response = new PendingMemberListResponse(totalPendingMemberCount,
                pendingMemberResponseList);

        return ApiResponse.success(response);
    }

    /**
     * 특정 PendingMember의 상세 정보 확인
     */
    @GetMapping("/{pendingMemberId}")
    @PendingMemberApiDocument.GetPendingMemberDetailApiDoc
    public ApiResponse<PendingMemberDetailResponse> getPendingMemberDetail(
            @PathVariable("pendingMemberId") UUID pendingMemberId) {
        PendingMemberDetailResponse response = pendingMemberService.getPendingMemberDetail(pendingMemberId);

        return ApiResponse.success(response);
    }


    /**
     * 회원가입 요청 단건 수락
     *
     * @param request PendingMember PK
     * @return 생성된 Member 객체
     */
    @PostMapping("")
    @PendingMemberApiDocument.AcceptPendingMemberApiDoc
    public ApiResponse<MemberResponse> acceptPendingMember(@RequestBody PendingMemberManageRequest request) {
        Member savedMember = pendingMemberService.acceptPendingMember(request.getPendingMemberId());
        MemberResponse response = memberMapper.toResponse(savedMember);

        return ApiResponse.success(response);
    }

}
