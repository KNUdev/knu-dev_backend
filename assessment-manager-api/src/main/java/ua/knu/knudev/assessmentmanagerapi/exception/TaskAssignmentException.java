package ua.knu.knudev.assessmentmanagerapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class TaskAssignmentException extends RuntimeException {
    private HttpStatus statusCode;

    public TaskAssignmentException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
