package ua.knu.knudev.knudevsecurity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ua.knu.knudev.knudevsecurityapi.security.AccountRole;

import java.util.Set;
import java.util.UUID;

@Table(schema = "security_management", name = "account_auth")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountAuth {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            schema = "security_management",
            name = "account_auth_role",
            joinColumns = @JoinColumn(name = "account_auth_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<AccountRole> roles;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false;
}
