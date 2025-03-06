package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.QProgramSectionMapping;

import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface ProgramSectionMappingRepository extends JpaRepository<ProgramSectionMapping, UUID> {
    QProgramSectionMapping qPSM = QProgramSectionMapping.programSectionMapping;

    List<ProgramSectionMapping> findByEducationProgramId(UUID educationProgramId);

    boolean existsByEducationProgram_IdAndSection_Id(UUID programId, UUID sectionId);

    default void removeProgramSectionMapping(UUID programId, UUID sectionId) {
        getQueryFactory().delete(qPSM)
                .where(
                        qPSM.educationProgram.id.eq(programId),
                        qPSM.section.id.eq(sectionId)
                )
                .execute();
    }
}
