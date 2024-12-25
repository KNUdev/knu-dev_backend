package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "technical_role", nullable = false)
    private AccountTechnicalRole technicalRole;

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

    @ManyToOne
    @JoinColumn(name = "joined_users", referencedColumnName = "id", nullable = false)
    private RecruitmentAnalytics recruitmentAnalytics;
}
