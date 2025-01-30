package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findProjectByName(MultiLanguageField name);

    boolean existsProjectByAvatarFilename(String avatarFilename);

    boolean existsProjectById(UUID id);

    boolean existsByStatus(ProjectStatus status);
}
