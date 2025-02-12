package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Defines the status of projects")
public enum ProjectStatus {
    @Schema(description = "Defines the project`s status when it is in tha planing stage")
    PLANNED,

    @Schema(description = "Defines the project's status when it is in the development stage")
    UNDER_DEVELOPMENT,

    @Schema(description = "Defines the project's status when it is completed and still under support")
    RELEASED,

    @Schema(description = "Project is receiving a major update that introduces significant changes or improvements over the previous version.")
    MAJOR_UPDATE,

    @Schema(description = "Project is in maintenance mode; only minor updates, bug fixes, and small enhancements are being applied.")
    MAINTENANCE,

    @Schema(description = "Project is no longer actively maintained and is considered deprecated.")
    DEPRECATED
}
