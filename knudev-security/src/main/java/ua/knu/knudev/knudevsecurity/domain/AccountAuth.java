package ua.knu.knudev.knudevsecurity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.knu.knudev.knudevcommon.constant.AccountAdministrativeRole;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevsecurity.security.AuthUserDetails;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table(schema = "security_management", name = "auth_account")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountAuth implements Serializable, AuthUserDetails {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, updatable = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "technical_role", nullable = false)
    private AccountTechnicalRole technicalRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "administrative_role")
    private AccountAdministrativeRole administrativeRole;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "is_non_locked", nullable = false)
    private boolean nonLocked;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(technicalRole, administrativeRole)
                .filter(Objects::nonNull)
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        throw new AccountAuthException("Account does not have username");
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public Set<AccountRole> getRoles() {
        return Stream.of(technicalRole, administrativeRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
