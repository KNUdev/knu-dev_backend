package ua.knu.knudev.teammanagerapi.dto;

import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import ua.knu.knudev.knudevcommon.constant.Expertise;

public record RecruitmentOpenRequest (
    @NotNull
    String recruitmentName,
    @NotNull
    Expertise expertise,
    @Embedded
    RecruitmentAutoCloseConditionsDto autoCloseConditions
    )
{}
