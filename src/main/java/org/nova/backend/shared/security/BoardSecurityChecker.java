package org.nova.backend.shared.security;

import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.stereotype.Component;

@Component
public class BoardSecurityChecker {

    /**
     * 사용자가 관리자 또는 회장인지 확인
     * @param member 사용자 객체
     * @return true면 관리자 또는 회장, false면 일반 사용자
     */
    public boolean isAdminOrPresident(Member member) {
        return member.getRole() == Role.ADMINISTRATOR || member.getRole() == Role.CHAIRMAN;
    }
}
