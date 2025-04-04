package ua.knu.knudev.teammanager.github;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.github.dto.GithubRepoDataDto;
import ua.knu.knudev.teammanager.mapper.AccountProfileMapper;
import ua.knu.knudev.teammanager.repository.AccountProfileRepository;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;
import ua.knu.knudev.teammanager.service.api.GithubManagementApi;
import ua.knu.knudev.teammanagerapi.dto.ReleaseParticipationDto;
import ua.knu.knudev.teammanagerapi.exception.AccountException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubIntegrationService implements GithubManagementApi {

    @Value("${github.api.organization-name}")
    private String organizationName;

    private final GithubApiClient githubApiClient;
    private final AccountProfileRepository accountProfileRepository;
    private final AccountProfileMapper accountProfileMapper;

    private static final String BASE_URL = "https://api.github.com";

    @Override
    @SneakyThrows
    public int getTotalUserCommitsCount(RetrieveGithubUserCommitsAmountRequest request) {
        AtomicInteger commitsFromAllReposAmount = new AtomicInteger();

        String allReposUrl = BASE_URL + "/orgs/" + organizationName + "/repos";
        JsonNode repos = githubApiClient.invokeApi(allReposUrl);

        String commitsStartDate = transformLocalDateToISO8601(request.firstCommitDate());
        String commitsToDate = transformLocalDateToISO8601(request.lastCommitDate().plusDays(1));

        repos.forEach(repo -> {
            String repoName = repo.get("name").textValue();
            String defaultBranch = detectDefaultBranch(repoName);

            List<JsonNode> allCommits = getAllCommitsForUser(repoName, request.githubUsername(), defaultBranch,
                    commitsStartDate, commitsToDate, false);

            commitsFromAllReposAmount.addAndGet(allCommits.size());
        });

        return commitsFromAllReposAmount.get();
    }

    @Override
    public List<GithubRepoDataDto> getAllGithubRepos() {
        List<GithubRepoDataDto> githubRepoDataDtos = new ArrayList<>();
        String allReposUrl = BASE_URL + "/orgs/" + organizationName + "/repos";
        JsonNode repos = githubApiClient.invokeApi(allReposUrl);

        if (repos == null || !repos.isArray()) {
            log.error("No repos found!");
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        repos.forEach(repo -> {
            String repoName = repo.path("name").asText();
            LocalDate initializedAt = LocalDateTime.parse(repo.path("created_at").asText(), formatter).toLocalDate();
            LocalDateTime updatedAt = LocalDateTime.parse(repo.path("updated_at").asText(), formatter);
            String resourceUrl = repo.path("html_url").asText();
            List<String> contributors = getRepositoryContributorsByRepoName(repoName);

            GithubRepoDataDto githubRepoDataDto = GithubRepoDataDto.builder()
                    .name(repoName)
                    .initializedAt(initializedAt)
                    .lastUpdatedAt(updatedAt)
                    .contributors(contributors)
                    .resourceUrl(resourceUrl)
                    .build();

            githubRepoDataDtos.add(githubRepoDataDto);
        });

        return githubRepoDataDtos;
    }

    @Override
    public UserCommitsDto getRepoUserCommitsCount(String username, String repoName) {
        String defaultBranch = detectDefaultBranch(repoName);
        List<JsonNode> allCommits = getAllCommitsForUser(repoName, username, defaultBranch,
                null, null, true);
        int totalCommits = allCommits.size();

        LocalDate lastCommitDate = null;

        if (!allCommits.isEmpty()) {
            String latestCommitDateStr = allCommits.get(0)
                    .get("commit")
                    .get("committer")
                    .get("date")
                    .asText();
            lastCommitDate = LocalDate.parse(latestCommitDateStr.substring(0, 10));
        }

        return UserCommitsDto.builder()
                .lastCommitDate(lastCommitDate)
                .totalCommits(totalCommits)
                .build();
    }

    @Override
    public Set<ReleaseDto> getReleaseInfo(String repoName) {
        String releaseUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName + "/releases";
        JsonNode releases = githubApiClient.invokeApi(releaseUrl);
        AtomicInteger previousReleaseIndex = new AtomicInteger(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Set<ReleaseDto> releaseDtos = new HashSet<>();

        if (releases == null || releases.isEmpty()) {
            log.warn("No releases found for repository: {}", repoName);
            return Collections.emptySet();
        }

        for (int i = 0; i < releases.size(); i++) {
            JsonNode release = releases.get(i);
            LocalDateTime releaseStartDateTime = parseDate(release.get("created_at"), formatter);
            LocalDateTime releaseFinishDateTime = parseDate(release.get("published_at"), formatter);
            String releaseFinishDate = transformLocalDateTimeToISO8601(releaseFinishDateTime);
            String defaultBranch = detectDefaultBranch(repoName);
            List<JsonNode> allCommitsInRelease;

            if (previousReleaseIndex.get() > releases.size() - 1) {
                allCommitsInRelease = getAllCommitsForUser(repoName, null, defaultBranch,
                        null, releaseFinishDate, false);
            } else {
                LocalDateTime previousReleasePublishedDate = parseDate(releases.get(previousReleaseIndex.get())
                        .get("published_at"), formatter);
                String releaseStartDate = transformLocalDateTimeToISO8601(previousReleasePublishedDate);
                allCommitsInRelease = getAllCommitsForUser(repoName, null, defaultBranch,
                        releaseStartDate, releaseFinishDate, false);
            }
            if (releaseStartDateTime == null) {
                log.warn("Skipping release due to missing dates in repository: {}", repoName);
                return null;
            }

            int aggregatedGitHubCommitCount = allCommitsInRelease.size();
            String version = Optional.ofNullable(release.get("name")).map(JsonNode::textValue).orElse("Unknown");
            String changeLogEn = Optional.ofNullable(release.get("body")).map(JsonNode::textValue).orElse("No changelog available.");
            List<ReleaseParticipationDto> releaseDevelopers = getReleaseParticipationDtos(allCommitsInRelease);
            previousReleaseIndex.getAndIncrement();

            ReleaseDto releaseDto = ReleaseDto.builder()
                    .initializedAt(releaseStartDateTime)
                    .releaseFinishDate(releaseFinishDateTime)
                    .version(version)
                    .changesLogEn(changeLogEn)
                    .aggregatedGithubCommitCount(aggregatedGitHubCommitCount)
                    .releaseDevelopers(releaseDevelopers)
                    .build();
            releaseDtos.add(releaseDto);
        }

        return releaseDtos;
    }

    private LocalDateTime parseDate(JsonNode dateNode, DateTimeFormatter formatter) {
        return Optional.ofNullable(dateNode)
                .map(JsonNode::asText)
                .filter(date -> !date.isEmpty())
                .map(date -> LocalDateTime.parse(date, formatter))
                .orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            HttpRequest request = githubApiClient.buildHttpRequest(URI.create(BASE_URL + "/users/" + username));
            githubApiClient.sendHttpResponse(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<ReleaseParticipationDto> getReleaseParticipationDtos(List<JsonNode> allCommitsInRelease) {
        Map<String, List<JsonNode>> githubUsername2Commits = allCommitsInRelease.stream()
                .collect(Collectors.groupingBy(
                        commit -> commit.get("author").get("login").textValue()
                ));

        return githubUsername2Commits.entrySet()
                .stream().map(entrySet -> {
                    int commitsCount = entrySet.getValue().size();
                    String githubUsername = entrySet.getKey();
                    AccountProfile accountProfile = accountProfileRepository.findAccountProfileByGithubAccountUsername(githubUsername)
                            .orElseThrow(() -> new AccountException("Account not found for GitHub username: " + githubUsername));
                    AccountProfileDto accountProfileDto = accountProfileMapper.toDto(accountProfile);

                    return ReleaseParticipationDto.builder()
                            .accountProfile(accountProfileDto)
                            .roleSnapshot(accountProfileDto.technicalRole())
                            .commitCount(commitsCount)
                            .build();
                }).toList();
    }

    public List<JsonNode> getAllCommitsForUser(String repoName, String username, String branch, String firstCommitDate,
                                               String lastCommitDate, boolean undated) {
        List<JsonNode> allCommits = new ArrayList<>();
        final int MAX_PAGES = 500;
        int page = 1;
        while (true) {
            String commitsUrl = buildUrlForBranchCommits(repoName, firstCommitDate, lastCommitDate, username, undated, branch, page);
            JsonNode commits = githubApiClient.invokeApi(commitsUrl);
            if (commits == null || commits.isEmpty() || page == MAX_PAGES) {
                break;
            }
            commits.forEach(allCommits::add);
            page++;
        }
        return allCommits;
    }

    private String buildUrlForBranchCommits(String repoName, String firstCommitDate, String lastCommitDate, String githubUsername,
                                            boolean isUndated, String branch, int page) {
        String allUserCommitsUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName
                + "/commits?sha="
                + branch
                + "&per_page=100"
                + "&page=" + page;

        if (githubUsername != null) {
            allUserCommitsUrl += "&author=" + githubUsername;
        }
        if (!isUndated) {
            if (firstCommitDate != null) {
                allUserCommitsUrl += "&since=" + firstCommitDate + "&until=" + lastCommitDate;
            } else {
                allUserCommitsUrl += "&until=" + lastCommitDate;
            }
        }
        return allUserCommitsUrl;
    }

    private String detectDefaultBranch(String repoName) {
        try {
            String repoUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName;
            JsonNode repoData = githubApiClient.invokeApi(repoUrl);
            return repoData.has("default_branch")
                    ? repoData.get("default_branch").asText()
                    : "master";
        } catch (Exception e) {
            return "master";
        }
    }

    private String transformLocalDateToISO8601(LocalDate date) {
        return date.atStartOfDay() + "Z";
    }

    private String transformLocalDateTimeToISO8601(LocalDateTime dateTime) {
        return dateTime.atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private List<String> getRepositoryContributorsByRepoName(String repoName) {
        List<String> contributors = new ArrayList<>();
        JsonNode contributorsNode = githubApiClient.invokeApi(BASE_URL + "/repos/" + organizationName + "/" + repoName + "/contributors");

        contributorsNode.forEach(contributor -> {
            String nickname = contributor.get("login").textValue();
            contributors.add(nickname);
        });

        return contributors;
    }
}
