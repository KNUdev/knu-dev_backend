package ua.knu.knudev.fileserviceapi.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;

import javax.annotation.processing.FilerException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler
    public String handleAccountException(AccountException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    public String handleDepartmentException(DepartmentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(FileException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return exception.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    @ExceptionHandler
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return exception.getMessage();
    }

}
