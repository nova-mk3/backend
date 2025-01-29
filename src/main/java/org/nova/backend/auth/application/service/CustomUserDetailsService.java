package org.nova.backend.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.auth.application.dto.dto.CustomUserDetails;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * 로그인 시 사용자 인증
     *
     * @param studentNumber 학번
     * @return CustomUserDetails
     * @throws UsernameNotFoundException 학번으로 Member를 찾을 수 없음.
     */
    @Override
    public UserDetails loadUserByUsername(final String studentNumber) throws UsernameNotFoundException {

        Member memberData = memberRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> {
                    log.error("Member not found " + studentNumber);
                    return new UsernameNotFoundException("Member not found " + studentNumber);
                });

        return new CustomUserDetails(memberData);
    }
}
