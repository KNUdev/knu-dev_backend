package ua.knu.knudev.teammanagerapi.exception;

import org.springframework.http.HttpStatus;

public class ApiClientException extends RuntimeException {
    public ApiClientException(String message, HttpStatus httpStatus) {
        super(message + " " + httpStatus.value());
    }
}

