package ua.knu.knudev.teammanagerapi.api;

import ua.knu.knudev.teammanagerapi.request.DepartmentCreationRequest;

public interface DepartmentApi {

    void createDepartment(DepartmentCreationRequest departmentCreationRequest);
}
