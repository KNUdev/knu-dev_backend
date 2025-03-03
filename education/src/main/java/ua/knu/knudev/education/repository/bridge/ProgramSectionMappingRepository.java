package ua.knu.knudev.education.repository.bridge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.education.domain.bridge.ProgramSectionMapping;
import ua.knu.knudev.education.domain.program.ProgramSection;

import java.util.List;
import java.util.UUID;

public interface ProgramSectionMappingRepository extends JpaRepository<ProgramSectionMapping, UUID> {
    List<ProgramSectionMapping> findByEducationProgramId(UUID educationProgramId);

    ProgramSectionMapping findByEducationProgramAndSection(EducationProgram educationProgram, ProgramSection section);
    List<ProgramSectionMapping> findByEducationProgramAndOrderIndexBetween(EducationProgram program, int start, int end);

    @Modifying
    @Query("update ProgramSectionMapping psm set psm.orderIndex = psm.orderIndex + 1 " +
            "where psm.educationProgram = :program and psm.orderIndex between :start and :end")
    int incrementOrderIndexes(@Param("program") EducationProgram program,
                              @Param("start") int start,
                              @Param("end") int end);

    @Modifying
    @Query("update ProgramSectionMapping psm set psm.orderIndex = psm.orderIndex - 1 " +
            "where psm.educationProgram = :program and psm.orderIndex between :start and :end")
    int decrementOrderIndexes(@Param("program") EducationProgram program,
                              @Param("start") int start,
                              @Param("end") int end);
}
