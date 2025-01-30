package ua.knu.knudev.educationapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class EducationProgramException extends RuntimeException {
    private HttpStatus statusCode;

    public EducationProgramException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
