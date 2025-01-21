package ua.knu.knudev.teammanager.service.api;

import ua.knu.knudev.teammanagerapi.request.ReceiveUserCommitsAmountRequest;

public interface GitHubManagementApi {

    int retrieveUserCommits(ReceiveUserCommitsAmountRequest request);
}
