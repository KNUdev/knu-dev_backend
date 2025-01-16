package ua.knu.knudev.teammanagerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "Object that contains data for automatically close of recruitment")
@Builder
public record RecruitmentAutoCloseConditionsDto(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Schema(description = "Dead line date", requiredMode = Schema.RequiredMode.REQUIRED, implementation = LocalDateTime.class)
        @NotNull(message = "Deadline date must not be null.")
        @Future(message = "Deadline date must be in the future.")
        LocalDateTime deadlineDate,

        @Schema(description = "Maximum candidates amount", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
        @NotNull(message = "Maximum number of candidates must not be null.")
        @Min(value = 1, message = "There must be at least one candidate.")
        Integer maxCandidates
) {
}
