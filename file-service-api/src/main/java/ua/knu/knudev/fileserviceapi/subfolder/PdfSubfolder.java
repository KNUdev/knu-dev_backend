package ua.knu.knudev.fileserviceapi.subfolder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PdfSubfolder implements FileSubfolder {
    INTERN_ROLE_TASKS("/tasks/campus/intern-role-tasks"),
    DEVELOPER_ROLE_TASKS("/tasks/campus/developer-role-tasks"),
    PREMASTER_ROLE_TASKS("/tasks/campus/premaster-role-tasks"),
    MASTER_ROLE_TASKS("/tasks/campus/master-role-tasks"),
    TECHLEAD_ROLE_TASKS("/tasks/campus/techlead-role-tasks"),

    PROGRAM_FINAL_TASKS("/education/programs/final-tasks"),
    PROGRAM_SECTION_FINAL_TASKS("/education/programs/section/tasks"),
    PROGRAM_MODULE_FINAL_TASKS("/education/programs/module/tasks"),
    PROGRAM_TOPIC_TASKS("/education/programs/topic/tasks"),

    ROLE_TASKS("/education/programs/%s/tasks");
    ;

    private final String subfolderPath;

    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }
}