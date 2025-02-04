package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.domain.model.valueobject.Role;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExecutiveHistory {

    @Id
    @Column(name = "executive_history_id")
    private UUID id;

    private int year;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
