package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column
    private String avatarFilename;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column
    private LocalDateTime lastRoleUpdateDate;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_code_name", referencedColumnName = "code_name", nullable = false)
    private Specialty specialty;
}
