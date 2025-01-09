package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Defines tags used to categorize projects")
public enum ProjectTag {
    @Schema(description = "Projects related to management activities")
    MANAGEMENT,

    @Schema(description = "Projects related to departments or organizational units")
    DEPARTMENTS,

    @Schema(description = "Projects involving financial aspects or budgeting")
    FINANCES

    // TODO ADD MORE TAGS IN FUTURE
}

