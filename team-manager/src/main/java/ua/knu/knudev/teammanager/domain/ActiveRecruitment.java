package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "active_recruitment")
@Builder
public class ActiveRecruitment {

    @Id
    @Column(nullable = false)
    private Expertise expertise;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private String name;

    @Embedded
    private RecruitmentAutoCloseConditions recruitmentAutoCloseConditions;

}
