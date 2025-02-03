package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.auth.application.service.SignUpService;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.application.mapper.PendingMemberMapper;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sign Up API", description = "회원가입 API 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class SignUpController {

    private final SignUpService signUpService;
    private final PendingMemberMapper pendingMemberMapper;

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청
     * @return 생성된 PendingMember response
     */
    @PostMapping()
    @AuthApiDocument.SignUpApiDoc
    public ApiResponse<PendingMemberResponse> signUp(@RequestBody SignUpRequest signUpRequest) {

        PendingMember savedPendingMember = signUpService.signUp(signUpRequest);
        PendingMemberResponse response = pendingMemberMapper.toResponse(savedPendingMember);

        return ApiResponse.success(response);
    }
}
