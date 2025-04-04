package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "release_participation")
@Builder
public class ReleaseParticipation {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "release_id", referencedColumnName = "id", nullable = false)
    private Release release;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private AccountTechnicalRole roleSnapshot;

    @Column(nullable = false)
    private Integer commitCount;
}