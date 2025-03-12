package org.nova.backend.email.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.email.domain.model.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, UUID> {

    Optional<EmailAuth> findByEmailAndCode(String email, String code);
}
