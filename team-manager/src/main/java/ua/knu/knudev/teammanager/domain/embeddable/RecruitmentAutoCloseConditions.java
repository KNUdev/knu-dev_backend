package ua.knu.knudev.teammanager.domain.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentAutoCloseConditions {
    private LocalDateTime deadlineDate;
    private int maxCandidates;
}
