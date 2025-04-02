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

    @Query("select m from Member m where m.isDeleted=false and m.role IS NOT NULL and m.studentNumber!= :adminStudentNumber")
    List<Member> findAllMembers(@Param("adminStudentNumber") String adminStudentNumber);

//    @Query("select m from Member m where m.name like %:name% and m.isDeleted=false and m.role IS NOT NULL and m.studentNumber!= :adminStudentNumber")
//    List<Member> findAllMembersByName(@Param("adminStudentNumber") String adminStudentNumber,
//                                      @Param("name") String name);

    @Query("select m from Member m left join fetch m.graduation where m.id=:id and m.isDeleted=false")
    Optional<Member> findMemberById(@Param("id") UUID member);

    @Query("select m from Member m where m.studentNumber=:studentNumber and m.isDeleted=false")
    Optional<Member> findByStudentNumber(@Param("studentNumber") String studentNumber);

    @Query("select case when count(m) > 0 then true else false end from Member m where (m.studentNumber=:studentNumber or m.email = :email) and m.isDeleted=false")
    boolean existsByStudentNumberOrEmail(@Param("studentNumber") String studentNumber, @Param("email") String email);

    // 재학중인 member : 휴학 x, 졸업 x
    @Query("select m from Member m where m.isGraduation=false and m.isAbsence=false and m.role IS NOT NULL and m.studentNumber != :adminStudentNumber")
    List<Member> findMembersInSchool(@Param("adminStudentNumber") String adminStudentNumber);

    // 졸업하지 않은 member
    @Query("select m from Member m where m.isGraduation=false and m.role IS NOT NULL and m.studentNumber != :adminStudentNumber")
    List<Member> findMembersExcludeGraduation();

    // 졸업생 조회
    @Query("select m from Member m where m.isGraduation=true and m.isDeleted=false and m.role IS NOT NULL and m.studentNumber != :adminStudentNumber")
    List<Member> findAllMembersByGraduation(@Param("adminStudentNumber") String adminStudentNumber);

    //grade 이상 학년의 재학생 조회
    @Query("select m from Member m where m.isGraduation=false and m.grade >= :grade and m.isDeleted=false and m.role IS NOT NULL and m.studentNumber != :adminStudentNumber")
    List<Member> findByGradeGreaterThan(@Param("grade") int grade,
                                        @Param("adminStudentNumber") String adminStudentNumber);

    //특정 grade 의 재학생 조회
    @Query("select m from Member m where m.grade = :grade and m.isGraduation=false and m.isDeleted=false and m.role IS NOT NULL and m.studentNumber != :adminStudentNumber")
    List<Member> findAllMembersByGrade(@Param("grade") int grade,
                                       @Param("adminStudentNumber") String adminStudentNumber);
}
