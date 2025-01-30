package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.TestDomain;

import java.util.UUID;

public interface TestRepository extends JpaRepository<TestDomain, UUID> {
    boolean existsTestDomainByEnName(String enName);
}
