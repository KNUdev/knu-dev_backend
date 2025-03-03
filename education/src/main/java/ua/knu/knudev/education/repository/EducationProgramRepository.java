package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.educationapi.dto.ProgramSummaryDto;

import java.util.List;
import java.util.UUID;

public interface EducationProgramRepository extends JpaRepository<EducationProgram, UUID> {

//    @Query("""
//        SELECT new ua.knu.knudev.educationapi.dto.ProgramSummaryDto(
//            ep.id,
//            MultiLanguageFieldDto(ep.name.en, ep.name.uk),
//            COUNT(DISTINCT psm.section.id),
//            COUNT(DISTINCT smm.module.id),
//            COUNT(DISTINCT mtm.topic.id),
//            ep.expertise,
//            ep.createdDate,
//            ep.lastModifiedDate,
//            ep.isPublished,
//            0
//        )
//        FROM EducationProgram ep
//        LEFT JOIN ProgramSectionMapping psm ON psm.educationProgram = ep
//        LEFT JOIN SectionModuleMapping smm ON smm.section = psm.section
//        LEFT JOIN ModuleTopicMapping mtm ON mtm.module = smm.module
//        GROUP BY ep.id, ep.name.en, ep.name.uk, ep.expertise, ep.createdDate, ep.lastModifiedDate, ep.isPublished
//        ORDER BY ep.createdDate DESC
//    """)
//    List<ProgramSummaryDto> findAllWithSummary();
}
