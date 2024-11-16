package ua.knu.knudev.fileserviceapi.subfolder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PdfSubfolder implements FileSubfolder {
    INTERN_ROLE_TASKS("/tasks/campus/intern-role-tasks"),
    DEVELOPER_ROLE_TASKS("/tasks/campus/developer-role-tasks"),
    TECHLEAD_ROLE_TASKS("/tasks/campus/techlead-role-tasks"),;

    private final String subfolderPath;

    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }
}