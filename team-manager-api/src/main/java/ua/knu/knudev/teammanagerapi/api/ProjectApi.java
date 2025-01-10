package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.util.Set;
import java.util.UUID;

public interface ProjectApi {

    void createProject(ProjectCreationRequest projectCreationRequest);

    void addDeveloperToProject(UUID accountProfileId, UUID projectId);

    void updateProjectStatus(UUID projectId, ProjectStatus newProjectStatus);

    ProjectDto getProjectById(UUID projectId);

    Set<ProjectDto> getProjects();

    void releaseProject(UUID projectId, String projectDomain);
}
