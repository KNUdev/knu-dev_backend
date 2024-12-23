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
@Table(schema = "team_management", name = "closed_recruitment")
@Builder
public class ClosedRecruitment {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Expertise expertise;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime closedAt;

    @Embedded
    private RecruitmentAutoCloseConditions recruitmentAutoCloseConditions;

}
