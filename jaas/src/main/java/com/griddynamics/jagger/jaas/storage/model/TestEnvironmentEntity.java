package com.griddynamics.jagger.jaas.storage.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEqualCollection;

@Entity
public class TestEnvironmentEntity {
    public enum TestEnvironmentStatus {
        PENDING, RUNNING
    }

    @Id
    private String environmentId;

    @OneToMany(mappedBy = "testEnvironmentEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TestSuiteEntity> testSuites;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TestEnvironmentStatus status = TestEnvironmentStatus.PENDING;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "runningTestSuite")
    private TestSuiteEntity runningTestSuite;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public List<TestSuiteEntity> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuiteEntity> testSuites) {
        this.testSuites = testSuites;
    }

    public TestEnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(TestEnvironmentStatus status) {
        this.status = status;
    }

    public TestSuiteEntity getRunningTestSuite() {
        return runningTestSuite;
    }

    public void setRunningTestSuite(TestSuiteEntity runningTestSuite) {
        this.runningTestSuite = runningTestSuite;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TestEnvironmentEntity that = (TestEnvironmentEntity) obj;

        if (!environmentId.equals(that.environmentId)) return false;
        if (testSuites != null ? !isEqualCollection(testSuites, that.testSuites) : that.testSuites != null) return false;
        if (status != that.status) return false;
        return runningTestSuite != null ? runningTestSuite.equals(that.runningTestSuite) : that.runningTestSuite == null;

    }

    @Override
    public int hashCode() {
        int result = environmentId.hashCode();
        result = 31 * result + (testSuites != null ? testSuites.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + (runningTestSuite != null ? runningTestSuite.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestEnvironmentEntity{"
                + "environmentId='" + environmentId + '\''
                + ", testSuites=" + testSuites
                + ", status=" + status
                + ", runningTestSuite=" + runningTestSuite
                + '}';
    }
}
