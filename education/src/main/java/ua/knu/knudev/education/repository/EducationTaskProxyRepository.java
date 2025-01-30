package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.EducationTaskProxy;

import java.util.Optional;
import java.util.UUID;

public interface EducationTaskProxyRepository extends JpaRepository<EducationTaskProxy, UUID> {
    Optional<EducationTaskProxy> findByTaskFilename(String taskFilename);
}
