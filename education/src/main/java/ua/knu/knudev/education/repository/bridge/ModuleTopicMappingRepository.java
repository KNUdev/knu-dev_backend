package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ModuleTopicMappingRepository extends JpaRepository<ModuleTopicMapping, UUID> {
    List<ModuleTopicMapping> findByModuleIdIn(Collection<UUID> moduleIds);
}
