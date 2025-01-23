package ua.knu.knudev.fileserviceapi.subfolder;


import lombok.RequiredArgsConstructor;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;
//import ua.knu.knudev.knudevcommon.constant.LearningUnit;
//import ua.knu.knudev.knudevcommon.constant.Role;

@RequiredArgsConstructor
public enum PdfSubfolder2 implements FileSubfolder {

    /**
     * A single enum constant for all role-based tasks.
     * We'll format the path using the 'Role' name at runtime.
     * Example usage:
     *   PdfSubfolder.ROLE_TASKS.buildSubfolderPath(Role.DEVELOPER)
     * => "/tasks/campus/developer-role-tasks"
     */
    ROLE_TASKS("/tasks/campus/%s-role-tasks"),

    /**
     * A single enum constant for final tasks of a learning unit.
     * Example usage:
     *   PdfSubfolder.LEARNING_UNIT_FINAL_TASKS.buildSubfolderPath(LearningUnit.PROGRAM)
     * => "/education/programs/program/final-tasks"
     *
     * Or you could separate them out if you prefer different patterns.
     */
    LEARNING_UNIT_FINAL_TASKS("/education/programs/%s/tasks"),

    /**
     * A single enum constant for normal tasks of a learning unit.
     * Example usage:
     *   PdfSubfolder.LEARNING_UNIT_TASKS.buildSubfolderPath(LearningUnit.MODULE)
     * => "/education/programs/module/tasks"
     */
    LEARNING_UNIT_TASKS("/education/programs/%s/tasks");

    private final String subfolderPath;

    /**
     * A default method (from FileSubfolder) to get the base path pattern.
     */
    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }

    /**
     * Builds the final subfolder path for a given Role.
     * Only valid if this enum constant is ROLE_TASKS; otherwise we throw an exception.
     */
    public String buildSubfolderPath(AccountTechnicalRole role) {
        if (this != ROLE_TASKS) {
            throw new UnsupportedOperationException(
                    "buildSubfolderPath(Role) is only supported by ROLE_TASKS enum constant."
            );
        }
        // Format with role in lowercase (or adapt to your naming)
        return String.format(subfolderPath, role.name().toLowerCase());
    }

    /**
     * Builds the final subfolder path for a given LearningUnit.
     * Only valid if this enum constant is one of the LEARNING_UNIT_* constants.
     */
    public String buildSubfolderPath(LearningUnit unit) {
        if (this == ROLE_TASKS) {
            throw new UnsupportedOperationException(
                    "buildSubfolderPath(LearningUnit) is not supported by ROLE_TASKS enum constant."
            );
        }
        // Insert the learning unit name in lowercase
        return String.format(subfolderPath, unit.name().toLowerCase());
    }
}

