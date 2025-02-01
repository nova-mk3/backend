package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입 요청 처리 API", description = "관리자가 회원가입 요청을 처리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member-requests")
public class PendingMemberController {

}
