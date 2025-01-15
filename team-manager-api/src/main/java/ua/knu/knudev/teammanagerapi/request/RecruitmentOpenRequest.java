package ua.knu.knudev.teammanagerapi.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentAutoCloseConditionsDto;

@Schema(description = "Object that contains data to open recruitment")
@Builder
public record RecruitmentOpenRequest(
        @Schema(description = "Recruitment name", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100,
                example = "New Year recruitment")
        @NotBlank(message = "Recruitment name must not be blank.")
        @Size(max = 100, message = "Recruitment name must not exceed 100 characters.")
        String recruitmentName,

        @Schema(description = "Expertise", requiredMode = Schema.RequiredMode.REQUIRED, implementation = Expertise.class,
                example = "BACKEND")
        @NotNull(message = "Expertise must not be null.")
        Expertise expertise,

        @Schema(description = "KNUdev unit", requiredMode = Schema.RequiredMode.REQUIRED, implementation = KNUdevUnit.class,
                example = "PRECAMPUS")
        @NotNull(message = "Unit must not be null.")
        KNUdevUnit unit,

        @Schema(description = "Recruitment auto close condition", requiredMode = Schema.RequiredMode.REQUIRED,
                implementation = RecruitmentAutoCloseConditionsDto.class)
        @NotNull(message = "Auto-close conditions must not be null.")
        @Valid
        RecruitmentAutoCloseConditionsDto autoCloseConditions
) {
}
