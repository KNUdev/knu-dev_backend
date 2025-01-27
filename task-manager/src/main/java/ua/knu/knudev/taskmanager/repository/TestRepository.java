package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.taskmanager.domain.Test;

import java.util.UUID;

public interface TestRepository extends JpaRepository<Test, UUID> {
    boolean existsTestByEnName(String name);
}
