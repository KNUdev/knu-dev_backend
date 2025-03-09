package ua.knu.knudev.educationapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ProgramException extends RuntimeException {
    private HttpStatus statusCode;

    public ProgramException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
