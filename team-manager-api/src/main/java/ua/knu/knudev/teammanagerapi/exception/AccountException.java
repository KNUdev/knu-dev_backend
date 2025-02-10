package ua.knu.knudev.teammanagerapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccountException extends RuntimeException {
    private HttpStatus status;

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
