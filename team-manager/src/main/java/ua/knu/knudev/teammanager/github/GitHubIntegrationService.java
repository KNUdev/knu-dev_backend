package ua.knu.knudev.teammanager.github;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.service.api.GitHubManagementApi;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GitHubIntegrationService implements GitHubManagementApi {

    @Value("${github.api.organization-name}")
    private String organizationName;

    private final GitHubApiClient githubApiClient;

    private static final String BASE_URL = "https://api.github.com";

    @Override
    @SneakyThrows
    public int retrieveUserCommits(RetrieveGithubUserCommitsAmountRequest request) {
        int commitsFromAllReposAmmount = 0;

        String allReposUrl = BASE_URL + "/orgs/" + organizationName + "/repos";
        JsonNode repos = githubApiClient.invokeApi(allReposUrl);

        String commitsStartDate = transformLocalDateToISO8601(request.firstCommitDate());
        String commitsToDate = transformLocalDateToISO8601(request.lastCommitDate().plusDays(1));

        for (JsonNode repo : repos) {
            String repoName = repo.get("name").textValue();
            String commitsUrl = buildUrlForMasterCommits(repoName, commitsStartDate, commitsToDate, request.gitHubUsername(), request.isUndated());

            JsonNode commits = githubApiClient.invokeApi(commitsUrl);
            commitsFromAllReposAmmount += commits.size();
        }

        return commitsFromAllReposAmmount;
    }

    private String buildUrlForMasterCommits(String repoName,
                                            String firstCommitDate,
                                            String lastCommitDate,
                                            String gitHubUsername,
                                            boolean isUndated) {
        String allUserCommitsUrl = BASE_URL + "/repos/" + organizationName + "/" + repoName +
                "/commits?author=" + gitHubUsername +
                "&sha=master";
        if (!isUndated) {
            allUserCommitsUrl = allUserCommitsUrl + "&since=" + firstCommitDate + "&until=" + lastCommitDate;
        }

        return allUserCommitsUrl;
    }

    private String transformLocalDateToISO8601(LocalDate date) {
        return date.atStartOfDay() + "Z";
    }
}
