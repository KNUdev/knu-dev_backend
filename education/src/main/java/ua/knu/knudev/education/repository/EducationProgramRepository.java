package ua.knu.knudev.education.repository;

import com.querydsl.core.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.QEducationProgram;
import ua.knu.knudev.education.domain.bridge.QModuleTopicMapping;
import ua.knu.knudev.education.domain.bridge.QProgramSectionMapping;
import ua.knu.knudev.education.domain.bridge.QSectionModuleMapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface EducationProgramRepository extends JpaRepository<EducationProgram, UUID> {
    QEducationProgram ep = QEducationProgram.educationProgram;
    QProgramSectionMapping psm = QProgramSectionMapping.programSectionMapping;
    QSectionModuleMapping smm = QSectionModuleMapping.sectionModuleMapping;
    QModuleTopicMapping mtm = QModuleTopicMapping.moduleTopicMapping;

    default Map<UUID, Tuple> fetchProgramSummariesIdCountMap() {
        List<Tuple> aggregator = getQueryFactory()
                .select(
                        ep.id,
                        psm.section.id.countDistinct(),
                        smm.module.id.countDistinct(),
                        mtm.topic.id.countDistinct()
                )
                .from(ep)
                .leftJoin(psm).on(psm.educationProgram.id.eq(ep.id))
                .leftJoin(smm).on(smm.section.id.eq(psm.section.id))
                .leftJoin(mtm).on(mtm.module.id.eq(smm.module.id))
                .groupBy(ep.id)
                .fetch();
        return aggregator.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(ep.id),
                        tuple -> tuple
                ));
    }

    default void deleteById(UUID programId) {
        getQueryFactory().delete(psm)
                .where(psm.educationProgram.id.eq(programId))
                .execute();

        getQueryFactory().delete(ep)
                .where(ep.id.eq(programId))
                .execute();
    }
}
