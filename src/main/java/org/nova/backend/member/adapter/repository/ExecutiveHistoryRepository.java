package org.nova.backend.member.adapter.repository;

import java.util.List;
import java.util.UUID;
import org.nova.backend.member.domain.model.entity.ExecutiveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutiveHistoryRepository extends JpaRepository<ExecutiveHistory, UUID> {

    @Query("select m.year from ExecutiveHistory m")
    List<Integer> findAllYears();

    List<ExecutiveHistory> getExecutiveHistoriesByYear(int year);
}
