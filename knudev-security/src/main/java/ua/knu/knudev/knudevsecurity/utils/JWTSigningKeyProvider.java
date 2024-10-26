package ua.knu.knudev.knudevsecurity.utils;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@Getter
@Scope("singleton")
public class JWTSigningKeyProvider {

    private final SecretKey signingKey;

    public JWTSigningKeyProvider() {
        String secretKey = "OblBr1u8BlwbqA61vt0yk4TC/mPlvT2O+t2m9SCci6g=";
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }
}
