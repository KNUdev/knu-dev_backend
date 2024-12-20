package ua.knu.knudev.rest.subfolder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PdfSubfolder implements FileSubfolder {
    INTERN_ROLE_TASKS("/tasks/campus/intern-role-tasks"),
    DEVELOPER_ROLE_TASKS("/tasks/campus/developer-role-tasks"),
    PREMASTER_ROLE_TASKS("/tasks/campus/premaster-role-tasks"),
    MASTER_ROLE_TASKS("/tasks/campus/master-role-tasks"),
    TECHLEAD_ROLE_TASKS("/tasks/campus/techlead-role-tasks"),;

    private final String subfolderPath;

    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }
}