package ua.knu.knudev.teammanager.service.api;

import ua.knu.knudev.teammanager.github.RetrieveGithubUserCommitsAmountRequest;

public interface GitHubManagementApi {

    int retrieveUserCommits(RetrieveGithubUserCommitsAmountRequest request);
}
