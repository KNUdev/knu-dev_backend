package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;

import java.util.UUID;

public interface ModuleTopicMappingRepository extends JpaRepository<ModuleTopicMapping, UUID> {
}
