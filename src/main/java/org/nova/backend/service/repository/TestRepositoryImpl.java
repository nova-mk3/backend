package org.nova.backend.service.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.nova.backend.service.entity.QTest;
import org.nova.backend.service.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestRepositoryImpl implements TestRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public TestRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Test> findTestsByAgeGreaterThan(int age) {
        QTest test = QTest.test;

        return queryFactory.selectFrom(test)
                .where(test.age.gt(age))
                .fetch();
    }
}
