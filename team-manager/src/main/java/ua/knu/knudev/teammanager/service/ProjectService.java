package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.github.dto.GitHubRepoDataDto;
import ua.knu.knudev.teammanager.github.dto.ReleaseDto;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;
import ua.knu.knudev.teammanager.mapper.ReleaseMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanager.service.api.GitHubManagementApi;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ua.knu.knudev.teammanager.domain.QSubproject.subproject;
import static ua.knu.knudev.teammanager.domain.SubprojectType.detectSubprojectType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

    private final ProjectRepository projectRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final ReleaseMapper releaseMapper;

    private final GitHubManagementApi gitHubManagementApi;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createOrModifyProject() {
        List<GitHubRepoDataDto> allGitHubRepos = gitHubManagementApi.getAllGitHubRepos();
        Set<Project> projectsToCreate = new HashSet<>();
        allGitHubRepos.forEach(repo -> processRepository(repo, projectsToCreate));
        saveProjects(projectsToCreate);
    }

    @Override
    public FullProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus) {
        return null;
    }

    @Override
    public FullProjectDto getById(UUID projectId) {
        return null;
    }

    @Override
    public Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize) {
        return null;
    }

    @Override
    public FullProjectDto addSubproject() {
        return null;
    }

    @Override
    public void addDevelopersToSubproject() {

    }

    private void processRepository(GitHubRepoDataDto repo, Set<Project> projectsToCreate) {
        String resourceUrl = repo.resourceUrl();
        List<String> contributors = repo.contributors();
        String[] repoNameParts = repo.name().split("_");

        validateRepositoryName(repoNameParts, repo.name());

        String projectName = repoNameParts[0];
        SubprojectType subprojectType = detectSubprojectType(repoNameParts[1]);

        Project project2Modify = findExistProject2Modify(projectName, projectsToCreate);
        Set<SubprojectAccount> subprojectAccounts = getSubprojectAccounts(contributors, repo.name());

        if (project2Modify != null) {
            addSubprojectIfNotExists(project2Modify, subprojectType, resourceUrl, subprojectAccounts, repo.name());
        } else {
            createNewProject(projectName, subprojectType, resourceUrl, repo.name(), subprojectAccounts, projectsToCreate, repo);
        }
    }

    private void validateRepositoryName(String[] repoNameParts, String repoName) {
        if (repoNameParts.length < 2) {
            throw new ProjectException("Repository has an invalid name: " + repoName);
        }
    }

    private Project findExistProject2Modify(String projectName, Set<Project> projectsToCreate) {
        return projectRepository.findProjectByName(projectName)
                .orElseGet(() -> projectsToCreate.stream()
                        .filter(project -> project.getName() != null && project.getName().equals(projectName))
                        .findFirst()
                        .orElse(null));
    }

    private Set<SubprojectAccount> getSubprojectAccounts(List<String> contributors, String repoName) {
        return contributors.stream()
                .map(contributor -> createSubprojectAccount(contributor, repoName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void addSubprojectIfNotExists(Project project, SubprojectType subprojectType, String resourceUrl,
                                          Set<SubprojectAccount> subprojectAccounts, String repoName) {
        Optional<Subproject> existingSubproject = project.getSubprojects().stream()
                .filter(subproject -> subproject.getType().equals(subprojectType))
                .findFirst();

        if (existingSubproject.isEmpty()) {
            Subproject newSubproject = buildSubproject(project, subprojectType, resourceUrl, repoName);
            newSubproject.getAllDevelopers().addAll(subprojectAccounts);
            project.getSubprojects().add(newSubproject);
        } else {
            Subproject subproject = existingSubproject.get();
            
            Set<SubprojectAccount> existingAccounts = subproject.getAllDevelopers();
            Set<SubprojectAccount> newAccounts = subprojectAccounts.stream()
                    .filter(account -> !existingAccounts.contains(account))
                    .collect(Collectors.toSet());
            existingAccounts.addAll(newAccounts);

            Set<ReleaseDto> newReleasesDtos = gitHubManagementApi.getReleaseInfo(repoName);
            Set<Release> newReleases = releaseMapper.toDomains(newReleasesDtos);

            Set<String> existingReleaseVersions = subproject.getReleases().stream()
                    .map(Release::getVersion)
                    .collect(Collectors.toSet());

            newReleases.stream()
                    .filter(release -> !existingReleaseVersions.contains(release.getVersion()))
                    .forEach(release -> {
                        release.setSubproject(subproject);
                        subproject.getReleases().add(release);
                    });
        }
    }

    private void createNewProject(String projectName, SubprojectType subprojectType, String resourceUrl, String repoName,
                                  Set<SubprojectAccount> subprojectAccounts, Set<Project> projectsToCreate, GitHubRepoDataDto repo) {
        Project newProject = new Project();

        newProject.setName(projectName);
        newProject.setStartedAt(repo.startedAt());
        newProject.setLastUpdatedAt(repo.lastUpdatedAt());
        newProject.setStatus(ProjectStatus.UNDER_DEVELOPMENT);

        Subproject subproject = buildSubproject(newProject, subprojectType, resourceUrl, repoName);
        subproject.getAllDevelopers().addAll(subprojectAccounts);
        newProject.getSubprojects().add(subproject);

        projectsToCreate.add(newProject);
    }

    public void saveProjects(Set<Project> projectsToCreate) {
        if (!projectsToCreate.isEmpty()) {
            projectRepository.saveAll(projectsToCreate);
        }
    }

    private SubprojectAccount createSubprojectAccount(String contributor, String repoName) {
        UserCommitsDto userCommitsDto = gitHubManagementApi.getUserCommitsDto(contributor, repoName);
        return accountProfileRepository.findByGitHubNickname(contributor)
                .map(accountProfile -> SubprojectAccount.builder()
                        .subproject(null)
                        .accountProfile(accountProfile)
                        .dateJoined(LocalDate.now())
                        .lastCommitDate(userCommitsDto.lastCommitDate())
                        .totalCommits(userCommitsDto.totalCommits())
                        .build())
                .orElse(null);
    }

    private Subproject buildSubproject(Project project, SubprojectType subprojectType, String resourceUrl, String repoName) {
        Set<ReleaseDto> releasesDtos = gitHubManagementApi.getReleaseInfo(repoName);

        Subproject subproject = Subproject.builder()
                .project(project)
                .type(subprojectType)
                .resourceUrl(resourceUrl)
                .allDevelopers(new HashSet<>())
                .releases(new HashSet<>())
                .build();

        project.getSubprojects().add(subproject);

        if (!releasesDtos.isEmpty()) {
            Set<Release> releases = releaseMapper.toDomains(releasesDtos);
            Set<Release> sortedReleases = releases.stream()
                    .peek(release -> release.setSubproject(subproject))
                    .sorted(Comparator.comparing(Release::getReleaseFinishDate).reversed())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            subproject.getReleases().addAll(sortedReleases);
        }

        return subproject;
    }


//    @Override
//    @Transactional
//    public FullProjectDto create(ProjectCreationRequest projectCreationRequest) {
//        MultiLanguageField name = multiLanguageFieldMapper.toDomain(projectCreationRequest.name());
//        MultiLanguageField description = multiLanguageFieldMapper.toDomain(projectCreationRequest.description());
//
//        String filename = imageServiceApi.uploadFile(
//                projectCreationRequest.avatarFile(),
//                name.getEn(),
//                ImageSubfolder.PROJECTS_AVATARS
//        );
//
//        Project project = Project.builder()
//                .name(name)
//                .description(description)
//                .avatarFilename(filename)
//                .tags(projectCreationRequest.tags())
//                .startedAt(null)
//                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
//                .status(ProjectStatus.PLANNED)
//                .build();
//
//        Project savedProject = projectRepository.save(project);
//        log.info("Project was created: {}", project.getId());
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @SneakyThrows
//    @Transactional
//    public FullProjectDto addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest) {
//        UUID projectId = addProjectDeveloperRequest.projectId();
//        UUID accountProfileId = addProjectDeveloperRequest.accountProfileId();
//        Project project = getProjectById(projectId);
//        AccountProfile accountProfile = accountProfileService.getDomainById(accountProfileId);
//
//        Set<SubprojectAccount> subprojectAccounts = project.getProjectAccounts();
//        SubprojectAccount subprojectAccount = createProjectAccount(
//                project,
//                accountProfile,
//                projectId,
//                accountProfileId
//        );
//
//        List<UUID> accountProfileIds = subprojectAccounts.stream()
//                .map(account -> account.getAccountProfile().getId())
//                .toList();
//
//        if (accountProfileIds.stream().anyMatch(id -> accountProfile.getId().equals(id))) {
//            throw new ProjectException("Account with ID: " + accountProfileId +
//                    " is already assigned to project: " + projectId);
//        }
//
//        subprojectAccounts.add(subprojectAccount);
//        Project savedProject = projectRepository.save(project);
//        log.info("Added developer: {}, to project: {}", subprojectAccount.getId().getAccountId(), projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus) {
//        if (newProjectStatus == null) {
//            throw new ProjectException("Project status can't be null!");
//        }
//
//        Project project = getProjectById(projectId);
//
//        if (project.getStatus() == ProjectStatus.PLANNED &&
//                newProjectStatus == ProjectStatus.UNDER_DEVELOPMENT) {
//            project.setStartedAt(LocalDate.now());
//        }
//
//        project.setStatus(newProjectStatus);
//        Project savedProject = projectRepository.save(project);
//        log.info("Project status updated: {}, in project: {}", newProjectStatus, projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto getById(UUID projectId) {
//        Project project = getProjectById(projectId);
//        return projectMapper.toDto(project);
//    }
//
//    @Override
//    @Transactional
//    public Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        Page<Project> allProjectsPage = projectRepository.findAll(pageable);
//
//        return allProjectsPage.map(project -> new ShortProjectDto(
//                multiLanguageFieldMapper.toDto(project.getName()),
//                multiLanguageFieldMapper.toDto(project.getDescription()),
//                project.getStatus(),
//                project.getAvatarFilename(),
//                project.getTags()
//        ));
//    }
//
//    @Override
//    @Transactional
//    public FullProjectDto release(UUID projectId, String projectDomain) {
//        Project project = getProjectById(projectId);
//
//        if (project.getReleaseInfo() != null) {
//            throw new ProjectException("Project already has a release!");
//        }
//        Release release = Release.builder()
//                .releaseDate(LocalDate.now())
//                .projectDomain(projectDomain)
//                .project(project)
//                .build();
//
//        project.setReleaseInfo(release);
//
//        Project savedProject = projectRepository.save(project);
//        log.info("Project released: {}", projectId);
//        return projectMapper.toDto(savedProject);
//    }
//
//    private Project getProjectById(UUID projectId) {
//        return projectRepository.findById(projectId).orElseThrow(
//                () -> new ProjectException("Project with id " + projectId + " not found"));
//    }
//
//    private SubprojectAccount createProjectAccount(Project project, AccountProfile accountProfile, UUID projectId, UUID accountProfileId) {
//        ProjectAccountId projectAccountId = new ProjectAccountId(projectId, accountProfileId);
//        return SubprojectAccount.builder()
//                .id(projectAccountId)
//                .accountProfile(accountProfile)
//                .project(project)
//                .dateJoined(LocalDate.now())
//                .build();
//    }

}
