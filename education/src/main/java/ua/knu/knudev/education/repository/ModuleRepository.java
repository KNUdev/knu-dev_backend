package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.program.ProgramModule;

import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ProgramModule, UUID> {
}
