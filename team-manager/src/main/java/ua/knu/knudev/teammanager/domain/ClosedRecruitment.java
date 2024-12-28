package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "closed_recruitment")
@Builder
public class ClosedRecruitment {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Expertise expertise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private KNUdevUnit type;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime closedAt;

    @Embedded
    private RecruitmentAutoCloseConditions recruitmentAutoCloseConditions;

    @OneToOne(mappedBy = "closedRecruitment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecruitmentAnalytics recruitmentAnalytics;

    public void setRecruitmentAnalytics(RecruitmentAnalytics recruitmentAnalytics) {
        if (recruitmentAnalytics == null) {
            if (this.recruitmentAnalytics != null) {
                this.recruitmentAnalytics.setClosedRecruitment(null);
            }
        } else {
            recruitmentAnalytics.setClosedRecruitment(this);
        }
        this.recruitmentAnalytics = recruitmentAnalytics;
    }
}
