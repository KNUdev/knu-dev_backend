package ua.knu.knudev.teammanagerapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SpecialtyException extends RuntimeException {
    public SpecialtyException(String message) {
        super(message);
    }
}
