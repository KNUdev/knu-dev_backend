package ua.knu.knudev.knudevsecurityapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class AccountRoleException extends RuntimeException {
    private HttpStatus statusCode;

    public AccountRoleException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
