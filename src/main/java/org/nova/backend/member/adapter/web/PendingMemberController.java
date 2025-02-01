package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.request.PendingMemberManageRequest;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.application.service.PendingMemberService;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
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
     * 회원가입 요청 단건 승인
     * @param request PendingMember PK
     * @return 생성된 Member 객체
     */
    @PostMapping("")
    public ApiResponse<MemberResponse> approvePendingMember(@RequestBody PendingMemberManageRequest request) {
        Member savedMember = pendingMemberService.approveMemberService(request.getPendingMemberId());
        MemberResponse response = memberMapper.toResponse(savedMember);

        return ApiResponse.success(response);
    }

}
