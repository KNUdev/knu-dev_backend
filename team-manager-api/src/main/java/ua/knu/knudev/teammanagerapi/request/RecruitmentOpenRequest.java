package ua.knu.knudev.teammanagerapi.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentAutoCloseConditionsDto;

public record RecruitmentOpenRequest(
        @NotBlank(message = "Recruitment name must not be blank.")
        @Size(max = 100, message = "Recruitment name must not exceed 100 characters.")
        String recruitmentName,

        @NotNull(message = "Expertise must not be null.")
        Expertise expertise,

        @NotNull(message = "Unit must not be null.")
        KNUdevUnit unit,

        @NotNull(message = "Auto-close conditions must not be null.")
        @Valid
        RecruitmentAutoCloseConditionsDto autoCloseConditions
) {
}
