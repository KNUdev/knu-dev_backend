package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Column(nullable = false, unique = true)
    private String email;
    private String avatarFilename;
    private String bannerFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "technical_role", nullable = false)
    private AccountTechnicalRole technicalRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Expertise expertise;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;
    private LocalDateTime lastRoleUpdateDate;

    @Column(nullable = false)
    private Integer yearOfStudyOnRegistration;

    @Column
    @Enumerated(EnumType.STRING)
    private KNUdevUnit unit;

    @Column(nullable = false)
    private String githubAccountUsername;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "specialty_code_name", referencedColumnName = "code_name", nullable = false)
    private Specialty specialty;

    //    TODO TEST THIS METHOD
    public Integer getCurrentYearOfStudy() {
        Integer baseStudyYears = this.yearOfStudyOnRegistration;
        LocalDate registrationYearEndDate = determineAcademicYearEndDate(this.registrationDate);
        LocalDate currentYearEndDate = determineAcademicYearEndDate(LocalDateTime.now());
        int additionalStudyYears = (int) ChronoUnit.YEARS.between(registrationYearEndDate, currentYearEndDate);
        return baseStudyYears + additionalStudyYears;
    }

    private LocalDate determineAcademicYearEndDate(LocalDateTime dateTime) {
        if (dateTime.getMonthValue() > 6) {
            return LocalDate.of(dateTime.getYear() + 1, 6, 30);
        } else {
            return LocalDate.of(dateTime.getYear(), 6, 30);
        }
    }
}
