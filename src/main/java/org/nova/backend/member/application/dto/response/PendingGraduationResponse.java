package org.nova.backend.member.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingGraduationResponse {

    private UUID id;
    private int year;
    private boolean contact;
    private boolean work;
    private String job;
    private String contactInfo;
    private String contactDescription;
}
