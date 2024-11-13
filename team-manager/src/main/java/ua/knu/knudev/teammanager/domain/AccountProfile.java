package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(schema = "team_management", name = "profile_account")
public class AccountProfile {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String middleName;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;
    private String avatarFilename;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            schema = "team_management",
            name = "profile_account_role",
            joinColumns = @JoinColumn(name = "profile_account_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<AccountRole> roles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Expertise expertise;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;
    private LocalDateTime lastRoleUpdateDate;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "specialty_code_name", referencedColumnName = "code_name", nullable = false)
    private Specialty specialty;
}
