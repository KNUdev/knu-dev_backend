package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.program.ProgramTopic;

import java.util.UUID;

public interface TopicRepository extends JpaRepository<ProgramTopic, UUID> {
}
