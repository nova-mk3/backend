package org.nova.backend.member.helper;

import java.util.concurrent.ThreadLocalRandom;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;

import java.util.UUID;

public class MemberFixture {

    private MemberFixture() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Member createStudent(ProfilePhoto profilePhoto) {
        return new Member(
                UUID.randomUUID(),
                generateRandomStudentNumber(),
                "password123!",
                "홍길동",
                generateRandomEmail(),
                false,
                4,
                2,
                false,
                false,
                profilePhoto,
                "010-1234-5678",
                "안녕하세요! 소프트웨어공학과 재학생입니다.",
                "2000-01-01",
                Role.GENERAL,
                null,
                false
        );
    }

    public static Member createGraduatedStudent() {
        return new Member(
                UUID.randomUUID(),
                generateRandomStudentNumber(),
                "password456!",
                "이몽룡",
                generateRandomEmail(),
                true,
                6,
                2,
                false,
                false,
                new ProfilePhoto(UUID.randomUUID(), "graduated.jpg", "https://example.com/photos/graduated.jpg"),
                "010-5678-1234",
                "졸업 후 IT 업계에서 일하고 있습니다.",
                "1998-05-05",
                Role.GENERAL,
                new Graduation(UUID.randomUUID(), 2022, true, true, "백엔드 개발자","https://tistory.com", "블로그 사이트"),
                false

        );
    }

    private static String generateRandomEmail() {
        return "hong" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private static String generateRandomStudentNumber() {
        long randomNumber = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        return String.valueOf(randomNumber);
    }
}
