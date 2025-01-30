package ua.knu.knudev.assessmentmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.assessmentmanager.domain.TestDomain;

import java.util.UUID;

public interface TestRepository extends JpaRepository<TestDomain, UUID> {
    boolean existsTestDomainByEnName(String enName);
}
