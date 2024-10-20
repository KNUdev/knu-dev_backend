package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import ua.knu.knudev.knudevsecurityapi.security.AccountRole;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "account_profile")
public class AccountProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String middleName;

//    todo add validation by @knu.ua
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private AccountRole accountRole;

    @Column
    @CreatedDate
    private LocalDate registrationDate;

    @Column
    @LastModifiedDate
    private LocalDate lastRoleUpdateDate;

//    @Column(nullable = false)
//    private Department department;
//
//    @Column(nullable = false)
//    private Specialty specialty;

    @Column
    private boolean isEnabled;

    @Column
    private boolean isLocked;
}
