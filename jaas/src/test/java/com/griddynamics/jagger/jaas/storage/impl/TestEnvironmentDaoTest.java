package com.griddynamics.jagger.jaas.storage.impl;


import com.griddynamics.jagger.jaas.config.TestPersistenceConfig;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestSuiteEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.RUNNING;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestPersistenceConfig.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TestEnvironmentDaoTest {
    private static final String ENVIRONMENT_ID_1 = "env1";
    private static final String ENVIRONMENT_ID_2 = "env2";
    private static final String TEST_SUITE_ID_1 = "test1";
    private static final String TEST_SUITE_ID_2 = "test2";
    private static final String TEST_SUITE_ID_3 = "test3";

    @Autowired
    private TestEnvironmentDao testEnvironmentDao;

    @Test
    public void readTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
    }

    @Test
    public void readAllTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        List<TestEnvironmentEntity> actuals = (List<TestEnvironmentEntity>) testEnvironmentDao.readAll();

        assertThat(testEnvironmentDao.readAll().size(), is(expected.size()));
        for (int i = 0; i < actuals.size(); i++) {
            assertThat(actuals.get(i), is(notNullValue()));
            assertThat(actuals.get(i), is(expected.get(i)));
        }
    }

    @Test
    public void updateTest() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setStatus(PENDING);
        expected.setRunningTestSuite(null);
        testEnvironmentDao.update(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Create_Test() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.createOrUpdate(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void createOrUpdate_Update_Test() {
        TestEnvironmentEntity expected = getTestEnvironmentEntity();
        testEnvironmentDao.create(expected);

        expected.setStatus(PENDING);
        expected.setRunningTestSuite(null);
        testEnvironmentDao.createOrUpdate(expected);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(notNullValue()));
        assertThat(actual, is(expected));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void deleteByIdTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);
        testEnvironmentDao.delete(ENVIRONMENT_ID_1);

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(nullValue()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void deleteTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);
        testEnvironmentDao.delete(expected.get(0));

        TestEnvironmentEntity actual = testEnvironmentDao.read(ENVIRONMENT_ID_1);

        assertThat(actual, is(nullValue()));
        assertThat(testEnvironmentDao.readAll().size(), is(1));
    }

    @Test
    public void countTest() {
        List<TestEnvironmentEntity> expected = getTestEnvironmentEntities();
        testEnvironmentDao.create(expected);

        int actual = (int) testEnvironmentDao.count();

        assertThat(actual, is(expected.size()));
    }


    private TestEnvironmentEntity getTestEnvironmentEntity() {
        TestEnvironmentEntity testEnvironmentEntity = new TestEnvironmentEntity();
        testEnvironmentEntity.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentEntity.setStatus(RUNNING);
        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_1);
        testSuiteEntity.setTestEnvironmentEntity(testEnvironmentEntity);
        testEnvironmentEntity.setTestSuites(newArrayList(testSuiteEntity));
        testEnvironmentEntity.setRunningTestSuite(testSuiteEntity);
        return testEnvironmentEntity;
    }

    private List<TestEnvironmentEntity> getTestEnvironmentEntities() {
        TestEnvironmentEntity testEnvironmentEntity1 = new TestEnvironmentEntity();
        testEnvironmentEntity1.setEnvironmentId(ENVIRONMENT_ID_1);
        testEnvironmentEntity1.setStatus(RUNNING);
        TestSuiteEntity testSuiteEntity = new TestSuiteEntity();
        testSuiteEntity.setTestSuiteId(TEST_SUITE_ID_1);
        testSuiteEntity.setTestEnvironmentEntity(testEnvironmentEntity1);
        testEnvironmentEntity1.setTestSuites(newArrayList(testSuiteEntity));
        testEnvironmentEntity1.setRunningTestSuite(testSuiteEntity);

        TestEnvironmentEntity testEnvironmentEntity2 = new TestEnvironmentEntity();
        testEnvironmentEntity2.setEnvironmentId(ENVIRONMENT_ID_2);
        testEnvironmentEntity2.setStatus(PENDING);
        TestSuiteEntity testSuiteEntity2 = new TestSuiteEntity();
        testSuiteEntity2.setTestSuiteId(TEST_SUITE_ID_2);
        testSuiteEntity2.setTestEnvironmentEntity(testEnvironmentEntity2);
        TestSuiteEntity testSuiteEntity3 = new TestSuiteEntity();
        testSuiteEntity3.setTestSuiteId(TEST_SUITE_ID_3);
        testSuiteEntity3.setTestEnvironmentEntity(testEnvironmentEntity2);
        testEnvironmentEntity2.setTestSuites(newArrayList(testSuiteEntity2, testSuiteEntity3));

        return newArrayList(testEnvironmentEntity1, testEnvironmentEntity2);
    }
}
