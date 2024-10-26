package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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
        AccountAuthDto account = accountService.findByEmail(authReq.email());

        checkAccountValidity(account, authReq.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authReq.email(), authReq.password())
        );

        Tokens tokens = jwtService.generateTokens(accountAuthMapper.toDomain(account));
        return AuthenticationResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }

    private void checkAccountValidity(AccountAuthDto account, String email) throws AuthenticationException {
        if (account == null) {
            String accDoesNotExistErrorMsg = String.format("Account with email %s does not exist", email);
            throw new UsernameNotFoundException(accDoesNotExistErrorMsg);
        }

        if (!account.enabled()) {
            String disabledAccMsg = "Your account is disabled. Please activate it via link on email: " + account.email();
            throw new DisabledException(disabledAccMsg);
        }

        if (!account.nonLocked()) {
            String lockedAccMsg = "Your account is locked. Please please contact support at EMAIL";
            throw new LockedException(lockedAccMsg);
        }
    }

}
