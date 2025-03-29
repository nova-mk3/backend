package org.nova.backend.member.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nova.backend.member.application.dto.request.UpdateGraduationRequest;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    // 졸업생 프로필 수정
    public void updateProfile(int year, UpdateGraduationRequest updateGraduationRequest) {
        this.year = year;
        this.isWork = updateGraduationRequest.isWork();
        this.job = updateGraduationRequest.getJob();
        this.isContact = updateGraduationRequest.isContact();
        this.contactInfo = updateGraduationRequest.getContactInfo();
        this.contactDescription = updateGraduationRequest.getContactDescription();
    }
}
