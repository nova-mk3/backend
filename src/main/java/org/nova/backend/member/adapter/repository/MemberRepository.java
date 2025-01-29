package org.nova.backend.member.adapter.repository;

import java.util.Optional;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);

}
