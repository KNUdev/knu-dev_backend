package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class RecruitmentAutoCloseConditions {

    private LocalDateTime deadlineDate;
    private Integer maxCandidates;
}
