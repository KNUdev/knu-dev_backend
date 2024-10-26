package ua.knu.knudev.knudevsecurityapi.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountAuthException extends RuntimeException {
    public AccountAuthException(String message) {
        super(message);
    }
}
