package ua.knu.knudev.teammanager.github;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.github.dto.GithubRepoDataDto;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;
import ua.knu.knudev.teammanager.service.api.GithubManagementApi;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final String BASE_URL = "https://api.github.com";

    @Override
    @SneakyThrows
    public int retrieveUserCommits(RetrieveGithubUserCommitsAmountRequest request) {
        AtomicInteger commitsFromAllReposAmount = new AtomicInteger();

        String allReposUrl = BASE_URL + "/orgs/" + organizationName + "/repos";
        JsonNode repos = githubApiClient.invokeApi(allReposUrl);

        String commitsStartDate = transformLocalDateToISO8601(request.firstCommitDate());
        String commitsToDate = transformLocalDateToISO8601(request.lastCommitDate().plusDays(1));

        repos.forEach(repo -> {
            String repoName = repo.get("name").textValue();
            String defaultBranch = detectDefaultBranch(repoName);
            String commitsUrl = buildUrlForBranchCommits(repoName, commitsStartDate, commitsToDate, request.githubUsername(), request.isUndated(), defaultBranch);

            JsonNode commits = githubApiClient.invokeApi(commitsUrl);
            commitsFromAllReposAmount.addAndGet(commits.size());
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
    public UserCommitsDto getUserCommitsDto(String username, String repoName) {
        String defaultBranch = detectDefaultBranch(repoName);
        String commitsUrl = buildUrlForBranchCommits(repoName, null, null, username, true, defaultBranch);

        JsonNode commits = githubApiClient.invokeApi(commitsUrl);
        int totalCommits = commits.size();
        LocalDate lastCommitDate = null;

        if (commits.isArray() && totalCommits > 0) {
            String latestCommitDateStr = commits.get(0).get("commit").get("committer").get("date").asText();
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

        if (releases == null || releases.isEmpty()) {
            log.warn("No releases found for repository: {}", repoName);
            return Collections.emptySet();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Set<ReleaseDto> releaseDtos = releases
                .findParents("id")
                .stream()
                .map(release -> {
                    LocalDateTime releaseStartDateTime = parseDate(release.get("created_at"), formatter);
                    LocalDateTime releaseFinishDateTime = parseDate(release.get("published_at"), formatter);

                    if (releaseStartDateTime == null || releaseFinishDateTime == null) {
                        log.warn("Skipping release due to missing dates in repository: {}", repoName);
                        return null;
                    }

                    LocalDate releaseStartDate = releaseStartDateTime.toLocalDate();
                    LocalDate releaseFinishDate = releaseFinishDateTime.toLocalDate();

                    String allCommitsPerPeriodUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName + "/commits" +
                            "?since=" + releaseStartDate + "T00:00:00Z" +
                            "&until=" + releaseFinishDate + "T23:59:59Z";

                    JsonNode commitsPerPeriod = githubApiClient.invokeApi(allCommitsPerPeriodUrl);
                    int aggregatedGitHubCommitCount = (commitsPerPeriod != null) ? commitsPerPeriod.size() : 0;

                    String version = Optional.ofNullable(release.get("name")).map(JsonNode::textValue).orElse("Unknown");
                    String changeLogEn = Optional.ofNullable(release.get("body")).map(JsonNode::textValue).orElse("No changelog available.");

                    return ReleaseDto.builder()
                            .initializedAt(releaseStartDateTime)
                            .releaseFinishDate(releaseFinishDateTime)
                            .version(version)
                            .changesLogEn(changeLogEn)
                            .aggregatedGithubCommitCount(aggregatedGitHubCommitCount)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

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

    private String buildUrlForBranchCommits(String repoName, String firstCommitDate, String lastCommitDate, String githubUsername, boolean isUndated, String branch) {
        String allUserCommitsUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName
                + "/commits?author="
                + githubUsername
                + "&sha=" + branch;
        if (!isUndated) {
            allUserCommitsUrl += "&since=" + firstCommitDate + "&until=" + lastCommitDate;
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
