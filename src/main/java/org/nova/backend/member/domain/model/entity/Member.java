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
import org.nova.backend.member.application.dto.request.UpdateMemberProfileRequest;
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
    private Graduation graduation;  //졸업 정보 저장

    private boolean isDeleted;  //회원 탈퇴 여부

    // 로그인 세션 생성 용 임시 객체
    public Member(String studentNumber, Role role) {
        this.name = studentNumber;
        this.role = role;
    }

    //회원 탈퇴
    public void setDeleted() {
        this.isDeleted = true;
    }

    // 비밀번호 변경
    public void updatePassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    // 이메일 변경
    public void updateEmail(String email) {
        this.email = email;
    }

    // 프로필 사진 변경
    public void updateProfilePhoto(ProfilePhoto profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    // 프로필 수정
    public void updateProfileInfo(UpdateMemberProfileRequest updateMemberProfileRequest) {
        this.name = updateMemberProfileRequest.getName();
        this.grade = updateMemberProfileRequest.getGrade();
        this.semester = updateMemberProfileRequest.getSemester();
        this.isAbsence = updateMemberProfileRequest.isAbsence();
        this.birth = updateMemberProfileRequest.getBirth();
        this.phone = updateMemberProfileRequest.getPhone();
        this.introduction = updateMemberProfileRequest.getIntroduction();
    }

    // 학기 +1
    public void updateSemester() {
        this.semester += 1;
    }

    // 학기 수정
    public void updateSemester(final int semester) {
        this.semester = semester;
    }

    // 학년 +1
    public void updateGrade() {
        this.grade += 1;
    }

    // 학년 수정
    public void updateGrade(final int grade) {
        this.grade = grade;
    }

    // 휴학 여부 토글
    public void updateAbsence() {
        this.isAbsence = !this.isAbsence;
    }

    // 휴학 여부 변경
    public void updateAbsence(boolean isAbsence) {
        this.isAbsence = isAbsence;
    }

    //졸업 여부 변경
    public void updateGraduation(boolean isGraduation) {
        this.isGraduation = isGraduation;
    }

    // 졸업 정보 변경
    public void updateGraduationInfo(Graduation graduation) {
        this.graduation = graduation;
    }

    // 권한 변경
    public void updateRole(Role role) {
        this.role = role;
    }

    // 일반회원으로 권한 변경
    public void updateRoleToGeneral() {
        this.role = Role.GENERAL;
    }

}
