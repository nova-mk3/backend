package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.application.dto.response.MemberDetailResponse;
import org.nova.backend.member.application.service.ExecutiveHistoryService;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * 연도 리스트 불러오기 : 모든 연도 조회
     */
    @GetMapping("/years")
    @ExecutiveHistoryApiDocument.GetYearListApiDoc
    public ResponseEntity<ApiResponse<List<Integer>>> getYears() {
        List<Integer> response = executiveHistoryService.getYears();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 연도 추가 : 2019년 ~ or 가장 최근 year 에서 1년 추가 임시 멤버(temp_data) 저장 이전 연도의 임원들 권한 변경
     */
    @PostMapping("/year")
    @ExecutiveHistoryApiDocument.AddYearApiDoc
    public ResponseEntity<ApiResponse<Integer>> addMaxYear() {
        int response = executiveHistoryService.addYear();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 연도 삭제 : 가장 최근 year에서 1년 삭제, 삭제된 year의 임원 이력 삭제
     */
    @DeleteMapping
    @ExecutiveHistoryApiDocument.DeleteYearApiDoc
    public ResponseEntity<ApiResponse<String>> deleteMaxYear() {
        executiveHistoryService.deleteYear();

        return ResponseEntity.ok().body(ApiResponse.success("연도 삭제 완료"));
    }

    /**
     * 임원 권한 변경
     */
    @PutMapping("/{executiveHistoryId}/{role}")
    @ExecutiveHistoryApiDocument.UpdateExecutivesRoleApiDoc
    public ResponseEntity<ApiResponse<ExecutiveHistoryResponse>> updateExecutiveRole(
            @PathVariable("executiveHistoryId") UUID executiveHistoryId, @PathVariable("role") Role role) {

        ExecutiveHistoryResponse response = executiveHistoryService.updateExecutiveRole(executiveHistoryId, role);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 특정 연도의 임원 이력 조회
     */
    @GetMapping("/{year}")
    @ExecutiveHistoryApiDocument.GetExecutiveHistoryByYearApiDoc
    public ApiResponse<List<ExecutiveHistoryResponse>> getExecutiveHistoryByYear(@PathVariable("year") int year) {
        List<ExecutiveHistoryResponse> response = executiveHistoryService.getExecutiveHistoryResponseByYear(year);
        return ApiResponse.success(response);
    }

    /**
     * 단건 임원 추가 : 특정 연도, role, 이름 또는 Member를 받음
     */
    @PostMapping("")
    @ExecutiveHistoryApiDocument.AddExecutiveHistoryApiDoc
    public ApiResponse<ExecutiveHistoryResponse> addExecutiveHistory(@RequestBody AddExecutiveHistoryRequest request) {

        ExecutiveHistoryResponse response = executiveHistoryService.addExecutiveHistory(request);

        return ApiResponse.success(response);
    }

    /**
     * 특정 임원 이력 삭제 : ExecutiveHistoryId pk
     */
    @DeleteMapping("/{executiveHistoryId}")
    @ExecutiveHistoryApiDocument.DeleteExecutiveHistoryApiDoc
    public ApiResponse<String> deleteExecutiveHistory(@PathVariable("executiveHistoryId") UUID executiveHistoryId) {
        executiveHistoryService.deleteExecutiveHistory(executiveHistoryId);

        return ApiResponse.success("임원 이력이 삭제되었습니다.");
    }

    /**
     * 회원 정보 단건 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/members/{memberId}")
    @ExecutiveHistoryApiDocument.GetMemberProfileApiDoc
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMemberProfile(@PathVariable("memberId") UUID memberId) {
        MemberDetailResponse response = memberService.getMemberProfile(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

//    /**
//     * 모든 회원 목록 불러오기
//     */
//    @GetMapping("/members")
//    @ExecutiveHistoryApiDocument.GetAllMembersApiDoc
//    public ApiResponse<List<MemberResponse>> getAllMembers() {
//
//        List<MemberResponse> response = memberService.getAllMembers();
//
//        return ApiResponse.success(response);
//    }

}
