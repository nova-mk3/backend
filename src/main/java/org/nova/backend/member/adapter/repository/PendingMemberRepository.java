package org.nova.backend.member.adapter.repository;

import java.util.UUID;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingMemberRepository extends JpaRepository<PendingMember, UUID> {

    boolean existsByStudentNumberOrEmail(String studentNumber, String email);
}
