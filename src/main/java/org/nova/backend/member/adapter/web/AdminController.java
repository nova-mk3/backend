package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.application.dto.request.UpdateMemberRequest;
import org.nova.backend.member.application.dto.response.AdminMemberResponse;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.service.AdminService;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 API", description = "관리자가 회원 정보를 관리하기 위한 api입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/members")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    /**
     * 전체 학기 일괄 증가 : 재학생들의 학기를 일괄 증가합니다. 졸업생, 휴학생 제외
     */
    @PutMapping("/semester")
    @AdminApiDocument.UpdateAllGradeApiDoc
    public ResponseEntity<ApiResponse<String>> updateAllSemesters() {

        adminService.updateAllSemesters();

        return ResponseEntity.ok().body(ApiResponse.success("전체 회원 학기 일괄 업데이트 완료"));
    }

    /**
     * 특정 회원의 휴학 여부 변경
     */
    @PutMapping("/{memberId}/absence")
    @AdminApiDocument.UpdateAbsenceApiDoc
    public ResponseEntity<ApiResponse<AdminMemberResponse>> updateAbsence(@PathVariable("memberId") UUID memberId,
                                                                          @RequestParam("isAbsence") boolean isAbsence) {

        Member member = memberService.updateAbsence(memberId, isAbsence);
        AdminMemberResponse response = adminService.getAdminMemberResponse(member);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 졸업 여부 변경
     */
    @PutMapping("/{memberId}/graduation")
    @AdminApiDocument.UpdateGraduationApiDoc
    public ResponseEntity<ApiResponse<AdminMemberResponse>> updateGraduation(@PathVariable("memberId") UUID memberId,
                                                                             @RequestParam("isGraduation") boolean isGraduation) {
        AdminMemberResponse response = adminService.updateGraduation(memberId, isGraduation);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 학년 변경
     */
    @PutMapping("/{memberId}/grade")
    @AdminApiDocument.UpdateGradeApiDoc
    public ResponseEntity<ApiResponse<AdminMemberResponse>> updateGrade(@PathVariable("memberId") UUID memberId,
                                                                        @RequestParam("grade") int grade) {
        Member member = memberService.updateGrade(memberId, grade);
        AdminMemberResponse response = adminService.getAdminMemberResponse(member);
        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 모든 회원 목록 불러오기
     */
    @GetMapping("")
    @AdminApiDocument.GetAllMembersApiDoc
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {

        List<MemberResponse> response = memberService.getAllMembers();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    /**
     * 이름으로 검색
     */
    @GetMapping("/name")
    @AdminApiDocument.FindMembersByNameApiDoc
    public ResponseEntity<ApiResponse<List<MemberResponse>>> findMembersByName(@RequestParam("name") String name) {

        List<MemberResponse> memberList = memberService.findMembersByName(name);

        return ResponseEntity.ok().body(ApiResponse.success(memberList));
    }

    /**
     * 회원 정보 단건 변경
     */
    @PutMapping("/{memberId}")
    @AdminApiDocument.UpdateMemberProfileApiDoc
    public ResponseEntity<ApiResponse<AdminMemberResponse>> updateMemberProfile(@PathVariable("memberId") UUID memberId,
                                                                                @RequestBody UpdateMemberRequest updateMemberRequest) {
        AdminMemberResponse response = adminService.updateMemberProfile(memberId, updateMemberRequest);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }


    /**
     * 동아리원이 아닌 사람 관리(회원 탈퇴)
     */
    @PutMapping("/{memberId}/deleted")
    @AdminApiDocument.DeleteMemberApiDoc
    public ResponseEntity<ApiResponse<String>> deleteMember(@PathVariable("memberId") UUID memberId) {
        adminService.deleteMember(memberId);

        return ResponseEntity.ok().body(ApiResponse.success("회원 탈퇴 완료"));
    }
}
