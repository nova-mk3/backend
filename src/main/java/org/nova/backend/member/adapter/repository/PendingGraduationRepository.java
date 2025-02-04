package org.nova.backend.member.adapter.repository;

import java.util.UUID;
import org.nova.backend.member.domain.model.entity.PendingGraduation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingGraduationRepository extends JpaRepository<PendingGraduation, UUID> {
}
