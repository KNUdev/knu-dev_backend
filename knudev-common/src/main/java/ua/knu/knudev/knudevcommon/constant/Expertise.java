package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Defines the expertise areas of an employee")
public enum Expertise {
    @Schema(description = "Full-stack development expertise")
    FULLSTACK,

    @Schema(description = "Back-end development expertise")
    BACKEND,

    @Schema(description = "Front-end development expertise")
    FRONTEND,

    @Schema(description = "UI/UX design expertise")
    UI_UX_DESIGNER
}

