package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PendingMember {

    @Id
    @Column(name = "pending_member_id")
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
    @JoinColumn(name="profile_photo_id")
    private ProfilePhoto profilePhoto;

    private String phone;

    private String introduction;

    private String birth;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pending_graduation_id")
    private PendingGraduation pendingGraduation;

    private boolean isRejected;

    //== 비즈니스 로직 ==//

    /**
     * 회원가입 요청 거절
     */
    public void rejectPendingMember() {
        this.isRejected = true;
    }
}
