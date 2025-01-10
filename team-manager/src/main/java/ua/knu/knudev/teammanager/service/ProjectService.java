package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.ProjectAccount;
import ua.knu.knudev.teammanager.domain.ProjectReleaseInfo;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.domain.embeddable.ProjectAccountId;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.mapper.ProjectMapper;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanagerapi.api.AccountProfileApi;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final ImageServiceApi imageServiceApi;
    private final AccountProfileApi accountProfileApi;
    private final AccountProfileMapper accountProfileMapper;

    @Override
    @Transactional
    public void createProject(ProjectCreationRequest projectCreationRequest) {
        MultiLanguageField name = projectMapper.map(projectCreationRequest.name());
        MultiLanguageField description = projectMapper.map(projectCreationRequest.description());

        String filename = imageServiceApi.uploadFile(
                projectCreationRequest.avatarFile(),
                name.getEn(),
                ImageSubfolder.PROJECTS_AVATARS
        );

        Project project = Project.builder()
                .name(name)
                .description(description)
                .avatarFilename(filename)
                .tags(projectCreationRequest.tags())
                .startedAt(LocalDate.now())
                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
                .build();

        projectRepository.save(project);
        log.info("Project was created: {}", project.getId());
    }

    @Override
    @SneakyThrows
    @Transactional
    public void addDeveloperToProject(UUID accountProfileId, UUID projectId) {
        validateInputs(accountProfileId, projectId);

        Project project = getById(projectId);
        AccountProfile accountProfile = getAccountProfileById(accountProfileId);

        ProjectAccount projectAccount = createProjectAccount(
                project,
                accountProfile,
                projectId,
                accountProfileId
        );

        project.getProjectAccounts().add(projectAccount);
        projectRepository.save(project);
        log.info("Added developer: {}, to project: {}", projectAccount.getId().getAccountId(), projectId);
    }

    @Override
    public void updateProjectStatus(UUID projectId, ProjectStatus newProjectStatus) {
        if (newProjectStatus == null) {
            throw new ProjectException("Project status can't be null!");
        }

        Project project = getById(projectId);
        project.setStatus(newProjectStatus);
        projectRepository.save(project);
        log.info("Project status updated: {}, in project: {}", newProjectStatus, projectId);
    }

    @Override
    public ProjectDto getProjectById(UUID projectId) {
        Project project = getById(projectId);
        return projectMapper.toDto(project);
    }

    @Override
    public Set<ProjectDto> getProjects() {
        Set<Project> allProjects = new HashSet<>(projectRepository.findAll());
        if (allProjects.isEmpty()) {
            throw new ProjectException("No projects found!");
        }
        log.info("Found {} projects", allProjects.size());
        return projectMapper.toDtos(allProjects);
    }

    @Override
    @Transactional
    public void releaseProject(UUID projectId, String projectDomain) {
        Project project = getById(projectId);

        if (project.getReleaseInfo() != null) {
            throw new ProjectException("Project already has a release!");
        }
        ProjectReleaseInfo projectReleaseInfo = ProjectReleaseInfo.builder()
                .releaseDate(LocalDate.now())
                .projectDomain(projectDomain)
                .project(project)
                .build();

        project.setReleaseInfo(projectReleaseInfo);

        projectRepository.save(project);
        log.info("Project released: {}", projectId);
    }

    private void validateInputs(UUID accountProfileId, UUID projectId) {
        if (accountProfileId == null || projectId == null) {
            throw new IllegalArgumentException("Account profile or project id cannot be null!");
        }
    }

    private Project getById(UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException("Project with id " + projectId + " not found"));
    }

    private AccountProfile getAccountProfileById(UUID accountProfileId) throws AccountNotFoundException {
        AccountProfileDto accountProfileDto = accountProfileApi.getById(accountProfileId);
        if (accountProfileDto == null) {
            throw new AccountNotFoundException("Account with id " + accountProfileId + " not found");
        }
        return accountProfileMapper.toDomain(accountProfileDto);
    }

    private ProjectAccount createProjectAccount(Project project, AccountProfile accountProfile, UUID projectId, UUID accountProfileId) {
        ProjectAccountId projectAccountId = new ProjectAccountId(projectId, accountProfileId);
        return ProjectAccount.builder()
                .id(projectAccountId)
                .accountProfile(accountProfile)
                .project(project)
                .dateJoined(LocalDate.now())
                .build();
    }

}
