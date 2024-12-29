package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record RecruitmentAutoCloseConditions(
        LocalDateTime deadlineDate,
        int maxCandidates
) {
}
