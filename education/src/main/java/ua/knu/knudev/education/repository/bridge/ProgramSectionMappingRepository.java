package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;

import java.util.List;
import java.util.UUID;

public interface ProgramSectionMappingRepository extends JpaRepository<ProgramSectionMapping, UUID> {
    List<ProgramSectionMapping> findByEducationProgramId(UUID programId);
}
