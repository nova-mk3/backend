package org.nova.backend.member.adapter.web;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${admin.student.number}")
    private String adminStudentNumber;
    @Value("${admin.password}")
    private String adminPassword;

    /**
     * 관리자 계정 생성
     */
    @Override
    @Transactional
    public void run(String... args)  {
        if (memberRepository.findByStudentNumber(adminStudentNumber).isEmpty()) {
            createAdminMember();
        }
    }

    /**
     * Role: 관리자인 Member 생성, 저장
     */
    private void createAdminMember(){
        Member member = new Member(
                UUID.randomUUID(),
                adminStudentNumber,
                bCryptPasswordEncoder.encode(adminPassword),
                "관리자",
                "nova@naver.com",
                false,
                0,
                0,
                false,
                null,
                "",
                "노바 홈페이지 관리자 입니다*^^*.",
                "20000000",
                Role.ADMINISTRATOR,
                null,
                false
        );

        member = memberRepository.save(member);
        log.info("관리자 계정 생성 : "+ member.getId());
    }
}
