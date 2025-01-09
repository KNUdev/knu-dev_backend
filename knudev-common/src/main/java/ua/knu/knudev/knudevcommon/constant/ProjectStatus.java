package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Defines the status of projects")
public enum ProjectStatus {
    @Schema(description = "Defines the project's status when it is in the development stage")
    UNDER_DEVELOPMENT,

    @Schema(description = "Defines the project's status when it is completed and still under support")
    FINISHED,

    @Schema(description = "Defines the project's status when it is under support after completion")
    UNDER_SUPPORT
}
