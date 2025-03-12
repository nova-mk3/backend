package org.nova.backend.member.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.ExecutiveHistoryRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.request.AddExecutiveHistoryRequest;
import org.nova.backend.member.application.dto.response.ExecutiveHistoryResponse;
import org.nova.backend.member.application.mapper.ExecutiveHistoryMapper;
import org.nova.backend.member.domain.exception.ExecutiveHistoryDomainException;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExecutiveHistoryService {

    private final ExecutiveHistoryRepository executiveHistoryRepository;
    private final MemberRepository memberRepository;

    private final ExecutiveHistoryMapper executiveHistoryMapper;

    private final MemberService memberService;

    /**
     * 연도 리스트 불러오기
     */
    public List<Integer> getYears() {
        List<Integer> years = executiveHistoryRepository.findAllYears();
        if (years.size() <= 1) {
            return years;
        }

        int minYear = years.stream().min(Integer::compare).orElseThrow();
        int maxYear = years.stream().max(Integer::compare).orElseThrow();

        return IntStream.rangeClosed(minYear, maxYear)
                .boxed().toList();
    }

    /**
     * 임원 추가 : 특정 연도, role, 이름 또는 Member를 받음
     */
    @Transactional
    public ExecutiveHistoryResponse addExecutiveHistory(AddExecutiveHistoryRequest request) {

        checkValidAddExecutiveHistoryRequest(request);

        Member member = null;
        if (request.getMemberId() != null) {
            member = memberService.findByMemberId(request.getMemberId());
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

        List<ExecutiveHistory> executiveHistoryList = executiveHistoryRepository.getExecutiveHistoriesByYear(year);

        return executiveHistoryList.stream().map(executiveHistoryMapper::toResponse).toList();
    }

    /**
     * 특정 임원 삭제
     */
    @Transactional
    public void deleteExecutiveHistory(final UUID executiveHistoryId) {
        ExecutiveHistory executiveHistory = findExecutiveHistory(executiveHistoryId);
        executiveHistoryRepository.delete(executiveHistory);
    }

    private ExecutiveHistory findExecutiveHistory(final UUID executiveHistoryId) {
        return executiveHistoryRepository.findById(executiveHistoryId)
                .orElseThrow(() -> new ExecutiveHistoryDomainException("ExecutiveHistory Not Found", HttpStatus.NOT_FOUND));
    }

}