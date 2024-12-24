package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Integer id;

    @OneToOne(mappedBy = "recruitmentAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    @MapsId
    private ClosedRecruitment closedRecruitment;

    @OneToMany(mappedBy = "recruitment_analytics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountProfile> joinedUsers;

}
