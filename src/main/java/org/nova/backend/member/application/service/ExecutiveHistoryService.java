package org.nova.backend.member.application.service;

import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.ExecutiveHistoryRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExecutiveHistoryService {

    private final ExecutiveHistoryRepository executiveHistoryRepository;
    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    /**
     * 연도 리스트 불러오기
     */
    public List<Integer> getYears(){
        List<Integer> years = executiveHistoryRepository.findAllYears();
        if(years.size()<=1){
            return years;
        }

        int minYear = years.stream().min(Integer::compare).orElseThrow();
        int maxYear = years.stream().max(Integer::compare).orElseThrow();

        return IntStream.rangeClosed(minYear, maxYear)
                .boxed().toList();
    }

    /**
     * 특정 연도의 임원들 불러오기
     */

    /**
     * 특정 role의 임원 추가
     */

    /**
     * 특정 role의 임원 삭제
     */

}