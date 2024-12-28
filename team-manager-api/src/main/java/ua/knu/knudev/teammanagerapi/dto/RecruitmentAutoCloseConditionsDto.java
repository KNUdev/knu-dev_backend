package ua.knu.knudev.teammanagerapi.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecruitmentAutoCloseConditionsDto(
        @NotNull(message = "Deadline date must not be null.")
        @FutureOrPresent(message = "Deadline date must be in the present or future.")
        LocalDateTime deadlineDate,

        @NotNull(message = "Maximum number of candidates must not be null.")
        @Min(value = 1, message = "There must be at least one candidate.")
        Integer maxCandidates
) {
}
