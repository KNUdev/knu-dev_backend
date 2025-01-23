package ua.knu.knudev.fileserviceapi.subfolder;

import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PdfSubfolder implements PdfSubfolderI {

    // Static subfolders with placeholders:
    ROLE_TASKS("/tasks/campus/%s-role-tasks"),
    LEARNING_UNIT_TASKS("/education/programs/%s/tasks");

    private final String pathPattern;

    // Cache for dynamic paths
    private static final Map<String, PdfSubfolderI> DYNAMIC_SUBFOLDERS = new ConcurrentHashMap<>();

    PdfSubfolder(String pattern) {
        this.pathPattern = pattern;
    }

    @Override
    public String getSubfolderPath() {
        return pathPattern;
    }

    // -------------- Dynamic Builders -------------- //

    public PdfSubfolderI toRole(AccountTechnicalRole technicalRole) {
        if (!this.name().contains("ROLE")) {
            throw new UnsupportedOperationException(
                    "toRole(...) not applicable to " + this.name()
            );
        }
        // Dynamically generate the path
        String dynamicPath = String.format(pathPattern, technicalRole.name().toLowerCase());

        // Retrieve or create a dynamic subfolder instance
        return getOrCreateDynamicSubfolder(dynamicPath);
    }

    public FileSubfolder toLearningUnitSubfolder(LearningUnit learningUnit) {
        if (!this.name().contains("LEARNING_UNIT")) {
            throw new UnsupportedOperationException(
                    "toLearningUnitSubfolder(...) not applicable to " + this.name()
            );
        }
        // Dynamically generate the path
        String dynamicPath = String.format(pathPattern, learningUnit.name().toLowerCase());

        // Retrieve or create a dynamic subfolder instance
        return getOrCreateDynamicSubfolder(dynamicPath);
    }

    // Create or retrieve a dynamic subfolder instance
    private PdfSubfolderI getOrCreateDynamicSubfolder(String dynamicPath) {
        return DYNAMIC_SUBFOLDERS.computeIfAbsent(dynamicPath, path -> () -> path);
    }
}
