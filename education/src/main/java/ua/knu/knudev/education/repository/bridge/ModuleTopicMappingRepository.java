package ua.knu.knudev.education.repository.bridge;

import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.bridge.ModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.QModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.QProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.QSectionModuleMapping;

import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface ModuleTopicMappingRepository extends JpaRepository<ModuleTopicMapping, UUID> {
    QProgramSectionMapping psm = QProgramSectionMapping.programSectionMapping;
    QSectionModuleMapping smm = QSectionModuleMapping.sectionModuleMapping;
    QModuleTopicMapping mtm = QModuleTopicMapping.moduleTopicMapping;

    List<ModuleTopicMapping> findByModuleIdIn(List<UUID> moduleIds);

    List<ModuleTopicMapping> findByProgram_IdAndSection_IdAndModule_IdAndTopic_Id(
            UUID programId, UUID sectionId, UUID moduleId, UUID topicId
    );

    default void removeModuleTopicMapping(UUID programId, UUID sectionId, UUID moduleId, UUID topicId) {
        getQueryFactory().delete(mtm)
                .where(
                        mtm.module.id.eq(moduleId),
                        mtm.topic.id.eq(topicId),
                        mtm.module.id.in(
                                JPAExpressions.select(smm.module.id)
                                        .from(smm)
                                        .join(psm).on(psm.section.id.eq(psm.section.id))
                                        .where(
                                                smm.section.id.eq(sectionId),
                                                psm.educationProgram.id.eq(programId)
                                        )
                        )
                )
                .execute();
    }
}
