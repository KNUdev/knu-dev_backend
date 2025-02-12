package ua.knu.knudev.knudevsecurityapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class AccountAuthException extends RuntimeException {
    private HttpStatus status;
    public AccountAuthException(String message) {
        super(message);
    }

    public AccountAuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
