package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.education.domain.program.ProgramSection;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SectionModuleMappingRepository extends JpaRepository<SectionModuleMapping, UUID> {
    List<SectionModuleMapping> findBySectionId(UUID sectionId);

    SectionModuleMapping findBySectionAndModule(ProgramSection section, ProgramModule module);


    // Optionally, to load modules for multiple sections at once:
    List<SectionModuleMapping> findBySectionIdIn(Collection<UUID> sectionIds);
}
