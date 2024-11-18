package ua.knu.knudev.teammanagerapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class DepartmentException extends RuntimeException{
    private HttpStatus statusCode;

    public DepartmentException(String message) {
        super(message);
    }

    public DepartmentException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}

