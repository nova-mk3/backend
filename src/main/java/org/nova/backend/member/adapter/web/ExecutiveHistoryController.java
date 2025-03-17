package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.service.ExecutiveHistoryService;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "임원 관리 API", description = "관리자가 임원을 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/executive-histories")
public class ExecutiveHistoryController {

    private final ExecutiveHistoryService executiveHistoryService;
    private final MemberService memberService;

    /**
     * 연도 리스트 불러오기
     */
    @GetMapping("/years")
    @ExecutiveHistoryApiDocument.GetYearListApiDoc
    public ApiResponse<List<Integer>> getYears() {

        List<Integer> response = executiveHistoryService.getYears();

        return ApiResponse.success(response);
    }

    /**
     * 특정 연도의 임원 이력 조회
     */
    @GetMapping("/{year}")
    @ExecutiveHistoryApiDocument.GetExecutiveHistoryByYearApiDoc
    public ApiResponse<List<ExecutiveHistoryResponse>> getExecutiveHistoryByYear(@PathVariable("year") int year) {
        List<ExecutiveHistoryResponse> response = executiveHistoryService.getExecutiveHistoryByYear(year);
        return ApiResponse.success(response);
    }

    /**
     * 임원 추가 : 특정 연도, role, 이름 또는 Member를 받음
     */
    @PostMapping("")
    @ExecutiveHistoryApiDocument.AddExecutiveHistoryApiDoc
    public ApiResponse<ExecutiveHistoryResponse> addExecutiveHistory(@RequestBody AddExecutiveHistoryRequest request) {

        ExecutiveHistoryResponse response = executiveHistoryService.addExecutiveHistory(request);

        return ApiResponse.success(response);
    }

    /**
     * 특정 임원 삭제 : ExecutiveHistoryId pk
     */
    @DeleteMapping("/{executiveHistoryId}")
    @ExecutiveHistoryApiDocument.DeleteExecutiveHistoryApiDoc
    public ApiResponse<String> deleteExecutiveHistory(@PathVariable("executiveHistoryId") UUID request) {
        executiveHistoryService.deleteExecutiveHistory(request);

        return ApiResponse.success("임원이 삭제되었습니다.");
    }

    /**
     * 모든 회원 목록 불러오기
     */
    @GetMapping("/members")
    @ExecutiveHistoryApiDocument.GetAllMembersApiDoc
    public ApiResponse<List<MemberResponse>> getAllMembers() {

        List<MemberResponse> response = memberService.getAllMembers();

        return ApiResponse.success(response);
    }

}
