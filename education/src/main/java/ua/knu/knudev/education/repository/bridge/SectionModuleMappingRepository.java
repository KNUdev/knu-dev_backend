package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;

import java.util.UUID;

public interface SectionModuleMappingRepository extends JpaRepository<SectionModuleMapping, UUID> {
}
