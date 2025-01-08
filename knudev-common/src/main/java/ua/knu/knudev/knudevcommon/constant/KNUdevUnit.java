package ua.knu.knudev.knudevcommon.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration representing different units in the KNUdev system.")
public enum KNUdevUnit {
    @Schema(description = "Represents the CAMPUS unit in KNUdev, where people work.")
    CAMPUS,

    @Schema(description = "Represents the PRECAMPUS unit in KNUdev, where people study.")
    PRECAMPUS
}

