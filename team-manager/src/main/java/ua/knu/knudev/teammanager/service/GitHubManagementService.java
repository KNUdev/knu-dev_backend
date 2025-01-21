package ua.knu.knudev.teammanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.service.api.GitHubManagementApi;
import ua.knu.knudev.teammanagerapi.request.ReceiveUserCommitsAmountRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.ConnectException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GitHubManagementService implements GitHubManagementApi {

    @Value("${github.api.access-token}")
    private String accessToken;
    @Value("${github.api.organization-name}")
    private String organizationName;
    private static final String BASE_URL = "https://api.github.com";

    @Override
    @SneakyThrows
    public int retrieveUserCommits(ReceiveUserCommitsAmountRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        int commitsFromAllReposAmmount = 0;
        URI allReposUri = URI.create(BASE_URL + "/orgs/" + organizationName + "/repos");

        HttpRequest organizationRequest = buildHttpRequest(allReposUri);
        HttpResponse<String> organizationCallResponse = sendHttpResponse(client, organizationRequest);

        JsonNode repos = new ObjectMapper().readTree(organizationCallResponse.body());

        String commitsStartDate = transformLocalDateToISO8601(request.firstCommitDate());
        String commitsToDate = transformLocalDateToISO8601(request.lastCommitDate().plusDays(1));

        for (JsonNode repo : repos) {
            String repoName = repo.get("name").textValue();
            String commitsUrl = buildUrlForMasterCommits(repoName, commitsStartDate, commitsToDate, request.gitHubUsername(), request.isUndated());

            HttpRequest commitRequest = buildHttpRequest(URI.create(commitsUrl));
            HttpResponse<String> commitsCallResponse = sendHttpResponse(client, commitRequest);

            int commitsAmount = new ObjectMapper().readTree(commitsCallResponse.body()).size();
            commitsFromAllReposAmmount += commitsAmount;
        }

        return commitsFromAllReposAmmount;
    }

    private HttpRequest buildHttpRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github.v3+json")
                .build();
    }

    private HttpResponse<String> sendHttpResponse(HttpClient client, HttpRequest request) throws InterruptedException, IOException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new ConnectException(response.statusCode() + " " + response.body());
        }

        return response;
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
