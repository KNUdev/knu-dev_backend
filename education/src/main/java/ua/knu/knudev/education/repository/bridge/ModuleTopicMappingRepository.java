package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramTopic;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ModuleTopicMappingRepository extends JpaRepository<ModuleTopicMapping, UUID> {
    List<ModuleTopicMapping> findByModuleId(UUID moduleId);

    ModuleTopicMapping findByModuleAndTopic(ProgramModule module, ProgramTopic topic);


    // Optionally, to load topics for multiple modules at once:
    List<ModuleTopicMapping> findByModuleIdIn(Collection<UUID> moduleIds);
}
