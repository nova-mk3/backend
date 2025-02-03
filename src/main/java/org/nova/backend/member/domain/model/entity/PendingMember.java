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

    private int year;

    private int semester;

    private boolean isAbsence;

    private String profilePhoto;

    private String phone;

    private String introduction;

    private String birth;

    @Enumerated(EnumType.STRING)
    private Role role;

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
