package org.nova.backend.email.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmailAuth {

    @Id
    @Column(name = "auth_code_id")
    private UUID id;

    private String email;

    private String code;

}
