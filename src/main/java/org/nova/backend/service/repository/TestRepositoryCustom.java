package org.nova.backend.service.repository;

import org.nova.backend.service.entity.Test;

import java.util.List;

public interface TestRepositoryCustom {
    List<Test> findTestsByAgeGreaterThan(int age);
}
