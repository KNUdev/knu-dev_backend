package ua.knu.knudev.teammanagerapi.api;

import org.springframework.data.domain.Page;
import ua.knu.knudev.knudevcommon.constant.ProjectStatus;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.request.AddProjectDeveloperRequest;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.util.UUID;

public interface ProjectApi {

    FullProjectDto create(ProjectCreationRequest projectCreationRequest);

    FullProjectDto addDeveloper(AddProjectDeveloperRequest addProjectDeveloperRequest);

    FullProjectDto updateStatus(UUID projectId, ProjectStatus newProjectStatus);

    FullProjectDto getById(UUID projectId);

    Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize);

    FullProjectDto release(UUID projectId, String projectDomain);
}
