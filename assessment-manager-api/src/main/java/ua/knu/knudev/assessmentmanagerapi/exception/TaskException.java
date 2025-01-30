package ua.knu.knudev.assessmentmanagerapi.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class TaskException extends RuntimeException {
    private HttpStatus statusCode;

    public TaskException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
