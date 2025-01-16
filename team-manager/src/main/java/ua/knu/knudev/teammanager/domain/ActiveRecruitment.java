package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.embeddable.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        schema = "team_management",
        name = "active_recruitment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"expertise", "unit"})
        },
        indexes = {
                @Index(name = "idx_expertise", columnList = "expertise"),
                @Index(name = "idx_unit", columnList = "unit")
        }
)
@Builder
public class ActiveRecruitment {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Expertise expertise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private KNUdevUnit unit;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private String name;

    @Embedded
    private RecruitmentAutoCloseConditions recruitmentAutoCloseConditions;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "active_recruitment_current_recruited",
            schema = "team_management",
            joinColumns = @JoinColumn(name = "active_recruitment_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "account_profile_id", referencedColumnName = "id", unique = true)
    )
    private Set<AccountProfile> currentRecruited;

    @Version
    private int version;

    public void joinUserToRecruitment(AccountProfile account) {
        if (ObjectUtils.isEmpty(account)) {
            throw new RecruitmentException("Cannot join empty user to recruitment with id: " + this.id);
        }
        this.currentRecruited.add(account);
    }

}
