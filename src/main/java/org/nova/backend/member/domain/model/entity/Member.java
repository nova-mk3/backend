package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.domain.model.valueobject.Role;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @Column(name = "member_id")
    private UUID id;

    @Column(unique = true)
    private String studentNumber;

    private String password;

    private String name;

    @Column(unique = true)
    private String email;

    private boolean isGraduation;

    private int grade;

    private int semester;

    private boolean isAbsence;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_photo_id")
    private ProfilePhoto profilePhoto;

    private String phone;

    private String introduction;

    private String birth;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "graduation_id")
    private Graduation graduation;

    private boolean isDeleted;

    // 로그인 세션 생성 용 임시 객체
    public Member(String studentNumber, Role role) {
        this.name = studentNumber;
        this.role = role;
    }
}
