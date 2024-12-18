package org.nova.backend.service.repository;

import org.nova.backend.service.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long>, TestRepositoryCustom {
}
