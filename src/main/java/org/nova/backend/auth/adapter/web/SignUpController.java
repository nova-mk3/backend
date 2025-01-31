package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.auth.application.dto.response.MemberResponse;
import org.nova.backend.auth.application.service.SignUpService;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.model.entity.Member;
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
    private final MemberMapper memberMapper;

    @PostMapping()
    public ApiResponse<MemberResponse> signUp(@RequestBody SignUpRequest signUpRequest) {

        Member savedMember = signUpService.createMember(signUpRequest);
        MemberResponse response = memberMapper.toResponse(savedMember);

        return ApiResponse.success(response);
    }
}
