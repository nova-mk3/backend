package org.nova.backend.member.adapter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutiveHistoryRepository extends JpaRepository<ExecutiveHistory, UUID> {

    @Query("SELECT COALESCE(MAX(m.year), :NOVA_FOUNDATION_YEAR) FROM ExecutiveHistory m")
    Integer findRecentYear(@Param("NOVA_FOUNDATION_YEAR") int NOVA_FOUNDATION_YEAR);

    @Query("SELECT DISTINCT eh.year FROM ExecutiveHistory eh ORDER BY eh.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT eh FROM ExecutiveHistory eh LEFT JOIN FETCH eh.member WHERE eh.year <= :year AND eh.member.role != 'GENERAL'")
    List<ExecutiveHistory> findPastExecutivesByYear(@Param("year") int year);

    @Query("SELECT eh FROM ExecutiveHistory eh LEFT JOIN FETCH eh.member WHERE eh.year = :year AND eh.role IS NOT NULL AND eh.member.studentNumber != :adminStudentNumber ")
    List<ExecutiveHistory> findExecutiveHistoriesByYear(@Param("year") int year,
                                                        @Param("adminStudentNumber") String adminStudentNumber);

    @Query("SELECT eh FROM ExecutiveHistory eh JOIN FETCH eh.member WHERE eh.id=:executiveHistoryId")
    Optional<ExecutiveHistory> findExecutiveHistoryWithMemberById(@Param("executiveHistoryId") UUID executiveHistoryId);
}
