package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.util.Set;
import java.util.UUID;

public interface ProjectApi {

    ProjectDto create(ProjectCreationRequest projectCreationRequest);

    ProjectDto addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest);

    ProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus);

    ProjectDto getById(UUID projectId);

    Set<ProjectDto> getAll();

    ProjectDto release(UUID projectId, String projectDomain);
}
