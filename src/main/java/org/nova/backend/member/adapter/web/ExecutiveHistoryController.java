package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.service.ExecutiveHistoryService;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "임원 관리 API", description = "관리자가 임원을 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/executiveHistories")
public class ExecutiveHistoryController {

    private final ExecutiveHistoryService executiveHistoryService;
    private final MemberService memberService;

    /**
     * 연도 리스트 불러오기
     */

    /**
     * 특정 연도의 임원들 불러오기
     */

    /**
     * 특정 role의 임원 추가
     */

    /**
     * 특정 role의 임원 삭제
     */

    /**
     * 모든 회원 목록 불러오기
     */
    @GetMapping("/members")
    public ApiResponse<List<MemberResponse>> getAllMembers() {

        List<MemberResponse> response = memberService.getAllMembers();

        return ApiResponse.success(response);
    }

}
