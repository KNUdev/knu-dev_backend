package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.dto.AccountAuthDto;
import ua.knu.knudev.knudevsecurity.mapper.AccountAuthMapper;
import ua.knu.knudev.knudevsecurityapi.api.AuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthServiceApi {
    private final AccountAuthService accountService;
    private final AccountAuthMapper accountAuthMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authReq) {
        AccountAuthDto account = accountService.getByEmail(authReq.email());

        checkAccountValidity(account, authReq.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authReq.email(), authReq.password())
        );

        AccountAuth accountDomain = accountAuthMapper.toDomain(account);
        Tokens tokens = jwtService.generateTokens(accountDomain);
        return AuthenticationResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }

    private void checkAccountValidity(AccountAuthDto account, String email) throws AuthenticationException {
        if (account == null) {
            throw new UsernameNotFoundException(
                    String.format("Account with email %s does not exist.", email)
            );
        }

        if (!account.enabled()) {
            throw new DisabledException(
                    "Your account is disabled. Please activate it via link on email: " + account.email()
            );
        }

        if (!account.nonLocked()) {
            throw new LockedException(
                    "Your account is locked. Please please contact support."
            );
        }
    }

}
