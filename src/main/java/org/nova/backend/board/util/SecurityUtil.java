package org.nova.backend.board.util;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final MemberRepository memberRepository;

    public UUID getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MemberDomainException("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
        }

        String studentNumber = authentication.getName();
        return memberRepository.findByStudentNumber(studentNumber)
                .map(Member::getId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}