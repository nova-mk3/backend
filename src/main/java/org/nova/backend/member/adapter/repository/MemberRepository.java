package org.nova.backend.member.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("select m from Member m where m.isDeleted=false")
    List<Member> findAllMembers();

    @Query("select m from Member m where m.id=:id and m.isDeleted=false")
    Optional<Member> findMemberById(@Param("id") UUID member);

    @Query("select m from Member m where m.studentNumber=:studentNumber and m.isDeleted=false")
    Optional<Member> findByStudentNumber(@Param("studentNumber") String studentNumber);

    @Query("select case when count(m) > 0 then true else false end from Member m where (m.studentNumber=:studentNumber or m.email = :email) and m.isDeleted=false")
    boolean existsByStudentNumberOrEmail(@Param("studentNumber") String studentNumber, @Param("email") String email);

}
