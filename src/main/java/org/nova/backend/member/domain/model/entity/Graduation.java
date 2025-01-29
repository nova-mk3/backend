package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Graduation {

    @Id
    @Column(name = "graduation_id")
    private UUID id;

    private int year;

    private boolean isContact;

    private boolean isWork;

    private String job;

    private String contactInfo;

    private String contactDescription;
}
