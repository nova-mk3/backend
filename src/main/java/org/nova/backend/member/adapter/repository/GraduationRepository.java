package org.nova.backend.member.adapter.repository;

import java.util.UUID;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GraduationRepository extends JpaRepository<Graduation, UUID> {

}
