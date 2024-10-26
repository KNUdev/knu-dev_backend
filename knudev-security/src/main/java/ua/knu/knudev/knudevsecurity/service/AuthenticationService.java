package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
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
        AccountAuth account = accountAuthMapper.toDomain(accountService.findByEmail(authReq.email()));

        accountService.checkAccountValidity(account, account.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authReq.email(), authReq.password())
        );

        Tokens tokens = jwtService.generateTokens(account);
        return AuthenticationResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .build();
    }

}
