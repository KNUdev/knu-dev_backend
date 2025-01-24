package ua.knu.knudev.teammanager.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.ConnectException;

@Component
@RequiredArgsConstructor
public class GitHubApiClient {

    @Value("${github.api.access-token}")
    private String accessToken;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @SneakyThrows
    public JsonNode invokeApi(String url) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                HttpRequest request = buildHttpRequest(URI.create(url));
                HttpResponse<String> response = sendHttpResponse(request);
                return objectMapper.readTree(response.body());
            } catch (IOException | InterruptedException e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw new RuntimeException("Max retries reached. Unable to complete the request.", e);
                }
                Thread.sleep(RETRY_DELAY_MS * attempt);
                System.out.println("Retrying request, attempt: " + attempt);
            }
        }
        throw new RuntimeException("Unexpected error while invoking API.");
    }

    private HttpRequest buildHttpRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github.v3+json")
                .build();
    }

    private HttpResponse<String> sendHttpResponse(HttpRequest request) throws InterruptedException, IOException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ConnectException("Failed with status code: " + response.statusCode() + ", body: " + response.body());
        }
        return response;
    }
}
