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

    boolean existsByEducationProgram_IdAndSection_IdAndModule_Id(UUID educationProgramId, UUID sectionId, UUID moduleId);

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

    default void adjustOrderIndexes(UUID programId, UUID sectionId) {
        List<SectionModuleMapping> mappings = getQueryFactory()
                .selectFrom(smm)
                .where(
                        smm.section.id.eq(sectionId),
                        smm.section.id.in(
                                JPAExpressions.select(psm.section.id)
                                        .from(psm)
                                        .where(psm.educationProgram.id.eq(programId))
                        )
                )
                .orderBy(smm.orderIndex.asc())
                .fetch();

        int newIndex = 1;
        for (SectionModuleMapping mapping : mappings) {
            getQueryFactory().update(smm)
                    .where(smm.id.eq(mapping.getId()))
                    .set(smm.orderIndex, newIndex)
                    .execute();
            newIndex++;
        }
    }

    default void removeSectionModuleMappingsBySectionId(UUID programId, UUID sectionId) {
        getQueryFactory().delete(smm)
                .where(
                        smm.section.id.eq(sectionId),
                        smm.section.id.in(
                                JPAExpressions.select(psm.section.id)
                                        .from(psm)
                                        .where(psm.educationProgram.id.eq(programId))
                        )
                )
                .execute();
    }

    default void removeAllByProgramId(UUID programId) {
        getQueryFactory().delete(smm)
                .where(smm.educationProgram.id.eq(programId))
                .execute();
    }

}
