package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

public interface ProjectApi {

    void createProject(ProjectCreationRequest projectCreationRequest);

}
