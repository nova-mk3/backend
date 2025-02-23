package org.nova.backend.board.util;

import java.util.Optional;
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

    /**
     * 현재 로그인한 사용자의 UUID를 반환 (인증되지 않은 경우 예외 발생)
     */
    public UUID getCurrentMemberId() {
        return getOptionalCurrentMemberId()
                .orElseThrow(() -> new MemberDomainException("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED));
    }

    /**
     * 현재 로그인한 사용자의 UUID를 Optional로 반환 (비로그인 사용자는 Optional.empty())
     */
    public Optional<UUID> getOptionalCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }

        String studentNumber = authentication.getName();

        return memberRepository.findByStudentNumber(studentNumber)
                .map(Member::getId);
    }

    /**
     * 현재 로그인한 사용자의 UUID를 반환 (로그인하지 않았을 경우 null 반환)
     */
    public UUID getCurrentMemberIdOrNull() {
        return getOptionalCurrentMemberId().orElse(null);
    }
}