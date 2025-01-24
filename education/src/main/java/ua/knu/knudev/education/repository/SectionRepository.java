package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.program.ProgramSection;

import java.util.UUID;

public interface SectionRepository extends JpaRepository<ProgramSection, UUID> {
}
