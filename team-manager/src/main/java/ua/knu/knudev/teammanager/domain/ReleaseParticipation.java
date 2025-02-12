package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "release_participation")
@Builder
public class ReleaseParticipation {
    @EmbeddedId
    private ReleaseParticipationId id;

    @ManyToOne
    @MapsId("releaseId")
    @JoinColumn(name = "release_id", referencedColumnName = "id", nullable = false)
    private Release release;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AccountTechnicalRole roleSnapshot;

    @Column(nullable = false)
    private Integer commitCount;
}
