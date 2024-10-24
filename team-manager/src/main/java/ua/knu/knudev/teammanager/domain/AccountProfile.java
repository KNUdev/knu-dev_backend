package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "team_management", name = "account_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
}
