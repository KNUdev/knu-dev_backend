package ua.knu.knudev.teammanager.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "subproject_account")
@Builder
public class    SubprojectAccount {

    @EmbeddedId
    private SubprojectAccountId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subprojectId")
    @JoinColumn(name = "subproject_id", referencedColumnName = "id", nullable = false)
    private Subproject subproject;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountProfile accountProfile;

    @Column(nullable = false)
    private LocalDate dateJoined;

    private LocalDate dateLeft;

    @Enumerated(EnumType.STRING)
    private AccountTechnicalRole currentRole;
}
