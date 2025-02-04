package org.nova.backend.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import org.nova.backend.member.domain.model.valueobject.Role;

@Getter
public class AddExecutiveHistoryRequest {

    @NotBlank
    private int year;

    @NotBlank
    private Role role;

    private String name;

    private UUID memberId;

}
