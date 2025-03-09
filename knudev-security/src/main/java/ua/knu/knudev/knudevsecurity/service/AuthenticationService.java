package ua.knu.knudev.knudevsecurity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurityapi.api.AuthServiceApi;
import ua.knu.knudev.knudevsecurityapi.dto.Tokens;
import ua.knu.knudev.knudevsecurityapi.exception.LoginException;
import ua.knu.knudev.knudevsecurityapi.exception.TokenException;
import ua.knu.knudev.knudevsecurityapi.request.AuthenticationRequest;
import ua.knu.knudev.knudevsecurityapi.response.AuthenticationResponse;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthServiceApi {
    private final AccountAuthService accountService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authReq) {
        Optional<AccountAuth> account = accountService.getDomainByEmail(authReq.email());
        checkAccountValidity(account);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authReq.email(), authReq.password())
            );
        } catch (BadCredentialsException e) {
            throw new LoginException();
        }

        Tokens tokens = jwtService.generateTokens(account.get());
        return AuthenticationResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization header is invalid"
            );
            return;
        }
        String refreshToken = authHeader.substring(7);

        try {
            if (jwtService.isAccessToken(refreshToken)) {
                throw new TokenException("Please enter refresh token", HttpStatus.BAD_REQUEST);
            }
        } catch (ExpiredJwtException ex) {
            throw new TokenException("Your token is expired. Please re-authenticate", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException | MalformedJwtException ex) {
            throw new TokenException("Your token is invalid", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(refreshToken);
        if (email != null) {
            Optional<AccountAuth> account = accountService.getDomainByEmail(email);
            if (account.isEmpty()) {
                throw new TokenException(
                        "The account associated with the provided token cannot be found. " +
                                "Please go to /authenticate to obtain a new token via authentication.",
                        HttpStatus.BAD_REQUEST
                );
            }

            if (jwtService.isTokenValid(refreshToken, account.get())) {
                String accessToken = jwtService.generateAccessToken(account.get());

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private void checkAccountValidity(Optional<AccountAuth> optionalAccount) throws AuthenticationException {
        if (optionalAccount.isEmpty()) {
            throw new LoginException();
        }
        AccountAuth account = optionalAccount.get();

        if (!account.isEnabled()) {
            throw new DisabledException(
                    "Your account is disabled. Please activate it via link on email: " + account.getEmail()
            );
        }

        if (!account.isNonLocked()) {
            throw new LockedException(
                    "Your account is locked. Please please contact support."
            );
        }
    }

}
