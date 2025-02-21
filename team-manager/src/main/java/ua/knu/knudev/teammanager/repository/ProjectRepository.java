package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    boolean existsByName(String name);

    Optional<Project> findProjectByName(String nameEn);

//    List<Project> findProjectByName(MultiLanguageField name);
//
//    boolean existsProjectByAvatarFilename(String avatarFilename);
//
//    boolean existsProjectById(UUID id);
//
//    boolean existsByStatus(ProjectStatus status);
}
