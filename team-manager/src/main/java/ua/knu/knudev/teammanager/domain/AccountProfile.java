package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column
    private String avatar;

//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(
//            schema = "team_management",
//            name = "account_profile_role",
//            joinColumns = @JoinColumn(name = "account_profile_id")
//    )
//    @Enumerated(EnumType.STRING)
//    @Column(name = "roles", nullable = false)
//    private Set<AccountRole> accountRole;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastRoleUpdateDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "specialty_code_name", referencedColumnName = "code_name", nullable = false)
    private Specialty specialty;
}
