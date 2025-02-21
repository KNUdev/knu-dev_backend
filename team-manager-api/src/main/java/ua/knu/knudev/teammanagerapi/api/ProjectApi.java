package ua.knu.knudev.teammanagerapi.api;

import org.springframework.data.domain.Page;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;
import ua.knu.knudev.teammanagerapi.dto.SubprojectDto;
import ua.knu.knudev.teammanagerapi.request.ProjectUpdateRequest;
import ua.knu.knudev.teammanagerapi.request.SubprojectUpdateRequest;

import java.util.UUID;

public interface ProjectApi {

    FullProjectDto updateProject(ProjectUpdateRequest projectUpdateRequest);

    SubprojectDto updateSubproject(SubprojectUpdateRequest subprojectUpdateRequest);

    FullProjectDto getById(UUID projectId);

    Page<ShortProjectDto> getAll(Integer pageNumber, Integer pageSize);

}
