package org.nova.backend.member.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.ExecutiveHistoryRepository;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.application.mapper.ExecutiveHistoryMapper;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.ExecutiveHistoryDomainException;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExecutiveHistoryService {

    @Value("${admin.student.number}")
    private String adminStudentNumber;

    private final int NOVA_FOUNDATION_YEAR = 2019;

    private final ExecutiveHistoryRepository executiveHistoryRepository;

    private final ExecutiveHistoryMapper executiveHistoryMapper;

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    /**
     * 연도 리스트 불러오기(내림차순)
     */
    public List<Integer> getYears() {

        return executiveHistoryRepository.findDistinctYears();
    }

    /**
     * 연도 추가 : 2019년 ~ or 가장 최근 year 에서 1년 추가 임시 객체 저장
     */
    @Transactional
    public int addYear() {
        int year = executiveHistoryRepository.findRecentYear(NOVA_FOUNDATION_YEAR - 1);  // 가장 최근 year
        // 연도 추가를 위한 ExecutiveHistory temp data 저장
        ExecutiveHistory tempForNewYear = createTempExecutiveHistory(year + 1);

        // 지난 임원들 권한 삭제
        updateExecutivesToGeneral(year);

        return tempForNewYear.getYear();
    }

    /**
     * 연도 추가를 위한 임시 객체 생성
     */
    private ExecutiveHistory createTempExecutiveHistory(int year) {
        ExecutiveHistory tempForYear = new ExecutiveHistory(
                UUID.randomUUID(),
                year + 1, null, "temp_data", null);
        return executiveHistoryRepository.save(tempForYear);
    }

    /**
     * 이전 연도 임원들 role GENERAL로 변경
     */
    private void updateExecutivesToGeneral(int year) {
        List<ExecutiveHistory> executivesList = executiveHistoryRepository.findPastExecutivesByYear(year);
        if (!executivesList.isEmpty()) {
            executivesList.forEach(executiveHistory -> {
                if (executiveHistory.getMember() != null) {
                    executiveHistory.getMember().updateRoleToGeneral();
                }
            });
        }
    }

    /**
     * 임원 이력 권한 변경
     */
    @Transactional
    public ExecutiveHistoryResponse updateExecutiveRole(UUID executiveHistoryId, Role role) {
        ExecutiveHistory executiveHistory = executiveHistoryRepository.findById(executiveHistoryId)
                .orElseThrow(() -> new ExecutiveHistoryDomainException("해당 임원이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (executiveHistory.getMember() == null) {  //member 권한 설정을 못하는 경우
            throw new ExecutiveHistoryDomainException("권한을 줄 수 없습니다.", HttpStatus.CONFLICT);
        }

        executiveHistory.updateRole(role);  //임원 권한 이력 수정

        Member executiveMember = executiveHistory.getMember();
        int year = executiveHistoryRepository.findRecentYear(NOVA_FOUNDATION_YEAR);  // 올해 연도 찾아오기

        if (year == executiveHistory.getYear()) {
            executiveMember.updateRole(role);  //올해 임원이면 권한 부여
        }

        return executiveHistoryMapper.toResponse(executiveHistory);
    }

    /**
     * 임원 추가 : 특정 연도, role, 이름 또는 Member를 받음
     */
    @Transactional
    public ExecutiveHistoryResponse addExecutiveHistory(AddExecutiveHistoryRequest request) {
        List<Integer> yearList = getYears();  // year 리스트 찾기

        if (!yearList.contains(request.getYear())) {  //year가 없으면 추가 x
            throw new ExecutiveHistoryDomainException("연도 추가 후 임원을 추가해 주세요.", HttpStatus.CONFLICT);
        }
        checkValidAddExecutiveHistoryRequest(request);

        Member member = null;
        if (request.getMemberId() != null) {
            member = memberService.findByMemberId(request.getMemberId());
            if (request.getYear() == yearList.getFirst()) {  // 멤버 권한 업데이트
                member.updateRole(request.getRole());
            }
        }

        ExecutiveHistory executiveHistory = executiveHistoryMapper.toEntity(request, member);
        ExecutiveHistory savedExecutiveHistory = executiveHistoryRepository.save(executiveHistory);

        return executiveHistoryMapper.toResponse(savedExecutiveHistory);
    }

    /**
     * 임원 이력 추가 시 임원 이름, 임원 객체 모두 입력하지 않은 경우
     *
     * @param request 임원 이력 추가 요청 객체
     */
    private void checkValidAddExecutiveHistoryRequest(AddExecutiveHistoryRequest request) {
        if (request.getName() == null && request.getMemberId() == null) {
            throw new ExecutiveHistoryDomainException("Invalid request. Please enter name or member.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 특정 연도의 임원 이력 조회
     */
    public List<ExecutiveHistoryResponse> getExecutiveHistoryByYear(final int year) {

        List<ExecutiveHistory> executiveHistoryList = executiveHistoryRepository.findExecutiveHistoriesByYear(year,
                adminStudentNumber);

        return executiveHistoryList.stream().map(executiveHistoryMapper::toResponse).toList();
    }

    /**
     * 특정 임원 이력 삭제
     */
    @Transactional
    public void deleteExecutiveHistory(final UUID executiveHistoryId) {
        ExecutiveHistory executiveHistory = findExecutiveHistory(executiveHistoryId);

        List<Integer> yearList = getYears();  // year 리스트 찾기

        // 올해의 임원 이력을 삭제하는 경우 member 권한 삭제 필요
        if (executiveHistory.getYear() == yearList.getFirst() && executiveHistory.getMember() != null) {
            executiveHistory.getMember().updateRoleToGeneral();
        }
        executiveHistoryRepository.delete(executiveHistory);
    }

    /**
     * 임원 기록 조회
     */
    private ExecutiveHistory findExecutiveHistory(final UUID executiveHistoryId) {
        return executiveHistoryRepository.findExecutiveHistoryWithMemberById(executiveHistoryId)
                .orElseThrow(
                        () -> new ExecutiveHistoryDomainException("ExecutiveHistory Not Found", HttpStatus.NOT_FOUND));
    }

}