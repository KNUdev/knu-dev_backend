package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.QProject;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findProjectByName(MultiLanguageField name);

    boolean existsProjectByAvatarFilename(String avatarFilename);

    boolean existsProjectById(UUID id);

    boolean existsByStatus(ProjectStatus status);

    QProject qProject = QProject.project;

    default List<Project> findProjectSByDeveloperAccountId(UUID developerAccountId) {
        return getQueryFactory().selectFrom(qProject)
                .where(qProject.projectAccounts.any()
                        .id.accountId.eq(developerAccountId))
                .fetch();
    }
}
