package ua.knu.knudev.knudevsecurity.security;

import org.springframework.security.core.userdetails.UserDetails;
import ua.knu.knudev.knudevcommon.constant.AccountRole;

import java.util.Set;
import java.util.UUID;

public interface AuthUserDetails extends UserDetails {
    UUID getId();

    String getEmail();

    Set<AccountRole> getRoles();
}
