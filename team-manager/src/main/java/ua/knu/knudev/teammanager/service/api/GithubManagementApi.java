package ua.knu.knudev.teammanager.service.api;

import ua.knu.knudev.teammanager.github.dto.GithubRepoDataDto;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;
import ua.knu.knudev.teammanager.github.RetrieveGithubUserCommitsAmountRequest;
import ua.knu.knudev.teammanager.github.dto.UserCommitsDto;

import java.util.List;
import java.util.Set;

public interface GithubManagementApi {

    int retrieveUserCommits(RetrieveGithubUserCommitsAmountRequest request);

    List<GithubRepoDataDto> getAllGithubRepos();

    UserCommitsDto getUserCommitsDto(String username, String repoName);

    Set<ReleaseDto> getReleaseInfo(String repoName);

    boolean existsByUsername(String username);

}
