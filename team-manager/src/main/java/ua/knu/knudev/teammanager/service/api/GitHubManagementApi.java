package ua.knu.knudev.teammanager.service.api;

import ua.knu.knudev.teammanager.github.dto.GitHubRepoDataDto;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;
import ua.knu.knudev.teammanager.github.RetrieveGithubUserCommitsAmountRequest;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;

import java.util.List;
import java.util.Set;

public interface GitHubManagementApi {

    int retrieveUserCommits(RetrieveGithubUserCommitsAmountRequest request);

    List<GitHubRepoDataDto> getAllGitHubRepos();

    UserCommitsDto getUserCommitsDto(String username, String repoName);

    Set<ReleaseDto> getReleaseInfo(String repoName);

    boolean existsByUsername(String username);

}
