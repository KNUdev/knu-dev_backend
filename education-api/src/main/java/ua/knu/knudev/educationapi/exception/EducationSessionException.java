package ua.knu.knudev.educationapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class EducationSessionException extends RuntimeException {
    private HttpStatus status;
    public EducationSessionException(String message) {
        super(message);
    }

    public EducationSessionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
