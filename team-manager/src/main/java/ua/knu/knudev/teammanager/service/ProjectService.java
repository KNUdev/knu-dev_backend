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
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
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
    public void create(ProjectCreationRequest projectCreationRequest) {
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
                .startedAt(null)
                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
                .status(ProjectStatus.PLANNED)
                .build();

        projectRepository.save(project);
        log.info("Project was created: {}", project.getId());
    }

    @Override
    @SneakyThrows
    @Transactional
    public void addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest) {
        UUID projectId = addProjectDeveloperRequest.projectId();
        UUID accountProfileId = addProjectDeveloperRequest.accountProfileId();
        Project project = getProjectById(projectId);
        AccountProfile accountProfile = getAccountProfileById(accountProfileId);
        Set<ProjectAccount> projectAccounts = project.getProjectAccounts();

        ProjectAccount projectAccount = createProjectAccount(
                project,
                accountProfile,
                projectId,
                accountProfileId
        );

        if (projectAccounts.contains(projectAccount)) {
            throw new ProjectException("Account with ID: " + accountProfile +
                    " is already assigned to project: " + projectId);
        }
        projectAccounts.add(projectAccount);
        projectRepository.save(project);
        log.info("Added developer: {}, to project: {}", projectAccount.getId().getAccountId(), projectId);
    }

    @Override
    public void updateStatus(UUID projectId, ProjectStatus newProjectStatus) {
        if (newProjectStatus == null) {
            throw new ProjectException("Project status can't be null!");
        }

        Project project = getProjectById(projectId);

        if (project.getStatus() == ProjectStatus.PLANNED &&
                newProjectStatus == ProjectStatus.UNDER_DEVELOPMENT) {
            project.setStartedAt(LocalDate.now());
        }

        project.setStatus(newProjectStatus);
        projectRepository.save(project);
        log.info("Project status updated: {}, in project: {}", newProjectStatus, projectId);
    }

    @Override
    public ProjectDto getById(UUID projectId) {
        Project project = getProjectById(projectId);
        return projectMapper.toDto(project);
    }

    @Override
    public Set<ProjectDto> getAll() {
        Set<Project> allProjects = new HashSet<>(projectRepository.findAll());
        if (allProjects.isEmpty()) {
            throw new ProjectException("No projects found!");
        }

        return projectMapper.toDtos(allProjects);
    }

    @Override
    @Transactional
    public void release(UUID projectId, String projectDomain) {
        Project project = getProjectById(projectId);

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

    private Project getProjectById(UUID projectId) {
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
