package ua.knu.knudev.education.repository.bridge;

import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.QProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.QSectionModuleMapping;
import ua.knu.knudev.education.domain.bridge.SectionModuleMapping;

import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface SectionModuleMappingRepository extends JpaRepository<SectionModuleMapping, UUID> {
    QProgramSectionMapping psm = QProgramSectionMapping.programSectionMapping;
    QSectionModuleMapping smm = QSectionModuleMapping.sectionModuleMapping;

    List<SectionModuleMapping> findBySectionIdIn(List<UUID> sectionIds);

    List<SectionModuleMapping> findByEducationProgram_IdAndSection_IdAndModule_Id(
            UUID programId, UUID sectionId, UUID moduleId
    );

    default void removeSectionModuleMapping(UUID programId, UUID sectionId, UUID moduleId) {
        getQueryFactory().delete(smm)
                .where(
                        smm.section.id.eq(sectionId),
                        smm.module.id.eq(moduleId),
                        smm.section.id.in(
                                JPAExpressions.select(psm.section.id)
                                        .from(psm)
                                        .where(psm.educationProgram.id.eq(programId))
                        )
                )
                .execute();
    }

}
