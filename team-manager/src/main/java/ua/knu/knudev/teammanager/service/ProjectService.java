package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.knudevcommon.constant.SubprojectType;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.*;
import ua.knu.knudev.teammanager.github.dto.GithubRepoDataDto;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;
import ua.knu.knudev.teammanager.mapper.*;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanager.repository.SubprojectRepository;
import ua.knu.knudev.teammanager.service.api.GithubManagementApi;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.dto.SubprojectDto;
import ua.knu.knudev.teammanagerapi.exception.ProjectException;
import ua.knu.knudev.teammanagerapi.request.ProjectUpdateRequest;
import ua.knu.knudev.teammanagerapi.request.SubprojectUpdateRequest;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.knu.knudev.knudevcommon.constant.SubprojectType.detectSubprojectType;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

    private final ProjectRepository projectRepository;
    private final AccountProfileRepository accountProfileRepository;
    private final SubprojectRepository subprojectRepository;
    private final ReleaseMapper releaseMapper;
    private final ProjectMapper projectMapper;
    private final MultiLanguageFieldMapper multiLanguageFieldMapper;
    private final AccountProfileMapper accountProfileMapper;
    private final SubprojectMapper subprojectMapper;
    private final SubprojectAccountMapper subprojectAccountMapper;
    private final GithubManagementApi gitHubManagementApi;

    @Scheduled(cron = "0 30 3 */3 * *")
    @Transactional
    public List<FullProjectDto> createOrModifyProject() {
        List<GithubRepoDataDto> allGitHubRepos = gitHubManagementApi.getAllGithubRepos();
        Set<Project> projectsToCreate = new HashSet<>();
        allGitHubRepos.forEach(repo -> processRepository(repo, projectsToCreate));
        List<Project> projects = saveProjects(projectsToCreate);
        return projectMapper.toDtos(projects);
    }

    @Override
    @Transactional
    public FullProjectDto getById(UUID projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectException("Project with id: " + projectId + " not found"));
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Project> projectsPage = projectRepository.findAll(pageable);

        return projectsPage.map(project -> {
            MultiLanguageFieldDto description = project.getDescription() != null
                    ? multiLanguageFieldMapper.toDto(project.getDescription())
                    : null;

            return ShortProjectDto.builder()
                    .name(project.getName())
                    .description(description)
                    .status(project.getStatus())
                    .tags(project.getTags())
                    .banner(project.getBanner())
                    .lastUpdate(project.getLastUpdatedAt())
                    .build();
        });
    }

    @Override
    @Transactional
    public FullProjectDto updateProject(ProjectUpdateRequest request) {
        Project project = projectRepository.findById(request.id())
                .orElseThrow(() -> new ProjectException("Project with id: " + request.id() + " not found!"));

        project.setName(getOrDefault(request.name(), project.getName()));
        project.setDescription(getOrDefault(request.description(), project.getDescription(), multiLanguageFieldMapper::toDomain));
        project.setBanner(getOrDefault(request.banner(), project.getBanner()));
        project.setStatus(getOrDefault(request.status(), project.getStatus()));

        project.setArchitect(mapIfNull(project.getArchitect(), request.architect(), accountProfileMapper::toDomain));
        project.setSupervisor(mapIfNull(project.getSupervisor(), request.supervisor(), accountProfileMapper::toDomain));

        if (request.tags() != null) {
            project.getTags().addAll(request.tags());
        }

        if (request.subprojects() != null) {
            Set<Subproject> subprojects = subprojectMapper.toDomains(request.subprojects());
            subprojects.forEach(subproject -> {
                subproject.associateReleasesWithSubproject(subproject.getReleases());
                subproject.associateDevelopersWithSubproject(subproject.getAllDevelopers());
                subproject.getReleases().forEach(release -> {
                    release.associateReleaseWithParticipations(release.getReleaseDevelopers());
                });
            });

            project.addSubprojects(subprojects);
        }

        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public SubprojectDto updateSubproject(SubprojectUpdateRequest request) {
        Subproject subproject = subprojectRepository.findById(request.id())
                .orElseThrow(() -> new ProjectException("Subproject with id: " + request.id() + " not found!"));

        subproject.addDevelopers(subprojectAccountMapper.toDomains(request.subprojectAccountDtos()));

        subproject = subprojectRepository.save(subproject);
        return subprojectMapper.toDto(subproject);
    }

    @Override
    @Transactional
    public List<ShortProjectDto> getAllByAccountId(UUID accountId) {
        if (Objects.isNull(accountId)) {
            throw new ProjectException("Account id cannot be null!");
        }
        List<Subproject> subprojects = subprojectRepository.findAllByAccountId(accountId);

        return subprojects.stream()
                .map(Subproject::getProject)
                .map(project -> ShortProjectDto.builder()
                        .name(project.getName())
                        .description(multiLanguageFieldMapper.toDto(project.getDescription()))
                        .status(project.getStatus())
                        .banner(project.getBanner())
                        .tags(project.getTags())
                        .lastUpdate(project.getLastUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    protected Boolean existsBySupervisorId(UUID supervisorId) {
        return projectRepository.existsProjectBySupervisorId(supervisorId);
    }

    protected Boolean existsByArchitectId(UUID architectId) {
        return projectRepository.existsProjectByArchitectId(architectId);
    }

    private void processRepository(GithubRepoDataDto repo, Set<Project> projectsToCreate) {
        String resourceUrl = repo.resourceUrl();
        List<String> contributors = repo.contributors();
        String[] repoNameParts = repo.name().split("_");

        validateRepositoryName(repoNameParts, repo.name());

        String projectName = repoNameParts[0];
        SubprojectType subprojectType = detectSubprojectType(repoNameParts[1]);

        Project project2Modify = findExistProject2Modify(projectName, projectsToCreate);

        if (project2Modify != null) {
            addSubprojectIfNotExists(project2Modify, subprojectType, resourceUrl, contributors, repo.name());
        } else {
            createNewProject(projectName, subprojectType, resourceUrl, repo.name(), contributors, projectsToCreate, repo);
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

    private Set<SubprojectAccount> getSubprojectAccounts(List<String> contributors, String repoName, Subproject subproject) {
        return contributors.stream()
                .map(contributor -> createSubprojectAccount(contributor, repoName, subproject))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void addSubprojectIfNotExists(Project project, SubprojectType subprojectType, String resourceUrl,
                                          List<String> contributors, String repoName) {
        Optional<Subproject> existingSubproject = project.getSubprojects().stream()
                .filter(subproject -> subproject.getType().equals(subprojectType))
                .findFirst();

        Subproject modifiedSubproject;

        if (existingSubproject.isEmpty()) {
            Subproject newSubproject = buildSubproject(project, subprojectType, resourceUrl, repoName);
            project.getSubprojects().add(newSubproject);
            modifiedSubproject = newSubproject;
        } else {
            Subproject subproject = existingSubproject.get();

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
            modifiedSubproject = subproject;
        }

        Set<SubprojectAccount> subprojectAccounts = getSubprojectAccounts(contributors, repoName, modifiedSubproject);

        Set<SubprojectAccount> existingAccounts = modifiedSubproject.getAllDevelopers();
        List<UUID> existingAccountsIds = existingAccounts.stream()
                .map(SubprojectAccount::getAccountProfile)
                .map(AccountProfile::getId)
                .toList();

        Set<SubprojectAccount> newAccounts = subprojectAccounts.stream()
                .filter(account2Create -> !existingAccountsIds.contains(account2Create.getAccountProfile().getId()))
                .collect(Collectors.toSet());

        if (!newAccounts.isEmpty()) {
            existingAccounts.addAll(newAccounts);
            modifiedSubproject.setAllDevelopers(existingAccounts);
        }

    }

    private void createNewProject(String projectName, SubprojectType subprojectType, String resourceUrl, String repoName,
                                  List<String> contributors, Set<Project> projectsToCreate, GithubRepoDataDto repo) {
        Project newProject = new Project();

        newProject.setName(projectName);
        newProject.setStartedAt(repo.initializedAt());
        newProject.setLastUpdatedAt(repo.lastUpdatedAt());
        newProject.setStatus(ProjectStatus.UNDER_DEVELOPMENT);

        Subproject subproject = buildSubproject(newProject, subprojectType, resourceUrl, repoName);
        newProject.getSubprojects().add(subproject);

        Set<SubprojectAccount> subprojectAccounts = getSubprojectAccounts(contributors, repoName, subproject);
        subproject.getAllDevelopers().addAll(subprojectAccounts);

        projectsToCreate.add(newProject);
    }

    public List<Project> saveProjects(Set<Project> projectsToCreate) {
        return projectRepository.saveAll(projectsToCreate);
    }

    private SubprojectAccount createSubprojectAccount(String contributor, String repoName, Subproject subproject) {
        UserCommitsDto userCommitsDto = gitHubManagementApi.getRepoUserCommitsCount(contributor, repoName);
        return accountProfileRepository.findAccountProfileByGithubAccountUsername(contributor)
                .map(accountProfile -> SubprojectAccount.builder()
                        .id(SubprojectAccountId.builder()
                                .subprojectId(subproject.getId())
                                .accountId(accountProfile.getId())
                                .build())
                        .subproject(subproject)
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

    private <T> T getOrDefault(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }

    private <T, R> R getOrDefault(T newValue, R currentValue, Function<T, R> mapper) {
        return newValue != null ? Objects.requireNonNullElse(mapper.apply(newValue), currentValue) : currentValue;
    }

    private <T, R> R mapIfNull(R currentValue, T newValue, Function<T, R> mapper) {
        return (currentValue == null && newValue != null) ? mapper.apply(newValue) : currentValue;
    }

}