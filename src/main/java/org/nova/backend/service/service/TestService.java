package org.nova.backend.service.service;

import org.nova.backend.service.entity.Test;
import org.nova.backend.service.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {
    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<Test> getTestsByAge(int age) {
        return testRepository.findTestsByAgeGreaterThan(age);
    }
}
