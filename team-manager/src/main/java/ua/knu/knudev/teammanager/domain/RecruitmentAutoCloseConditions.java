package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record RecruitmentAutoCloseConditions(
        LocalDateTime deadlineDate,
        Integer maxCandidates
) {

}
