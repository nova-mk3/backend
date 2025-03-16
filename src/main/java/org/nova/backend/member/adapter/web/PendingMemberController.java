package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.response.PendingMemberDetailResponse;
import org.nova.backend.member.application.dto.response.PendingMemberListResponse;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.member.application.service.PendingMemberService;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입 요청 처리 API", description = "관리자가 회원가입 요청을 처리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pending-members")
public class PendingMemberController {

    private final PendingMemberService pendingMemberService;
    private final MemberService memberService;

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
     *
     * @param pendingMemberId pk
     * @return PendingMemberDetailResponse
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
     * @param pendingMemberId pk
     * @return 생성된 Member 객체
     */
    @PostMapping("/{pendingMemberId}")
    @PendingMemberApiDocument.AcceptPendingMemberApiDoc
    public ApiResponse<MemberResponse> acceptPendingMember(@PathVariable("pendingMemberId") UUID pendingMemberId) {
        Member savedMember = pendingMemberService.acceptPendingMember(pendingMemberId);

        MemberResponse response = memberService.getMemberResponseFromMember(savedMember);

        return ApiResponse.success(response);
    }

    /**
     * 회원가입 요청 단건 반려
     *
     * @param pendingMemberId pk
     * @return 반려 완료
     */
    @PostMapping("/{pendingMemberId}/rejected")
    @PendingMemberApiDocument.RejectPendingMemberApiDoc
    public ApiResponse<String> rejectPendingMember(@PathVariable("pendingMemberId") UUID pendingMemberId) {
        pendingMemberService.rejectPendingMember(pendingMemberId);

        return ApiResponse.success("반려 완료");
    }

}
