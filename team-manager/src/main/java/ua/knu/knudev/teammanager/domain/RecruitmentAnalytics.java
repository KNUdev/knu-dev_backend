package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "recruitment_analytics")
@Builder
public class RecruitmentAnalytics {

    @Id
    @Column(nullable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ClosedRecruitment closedRecruitment;

    @OneToMany
    @JoinTable(
            name = "recruitment_analytics_joined_users",
            schema = "team_management",
            joinColumns = @JoinColumn(name = "recruitment_analytics_id"),
            inverseJoinColumns = @JoinColumn(name = "account_profile_id")
    )
    private List<AccountProfile> joinedUsers;

    // TODO: Add more analytics fields
}
