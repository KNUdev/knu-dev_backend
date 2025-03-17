package ua.knu.knudev.knudevrest.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;
import ua.knu.knudev.knudevsecurityapi.exception.LoginException;
import ua.knu.knudev.knudevsecurityapi.exception.TokenException;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class RestGlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler
    public ResponseEntity<String> handleAccountException(AccountException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(DepartmentException.class)
    public ResponseEntity<String> handleDepartmentException(DepartmentException exception) {
        MediaType mediaType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        HttpStatus statusCode = exception.getStatusCode() == null ? HttpStatus.CONFLICT : exception.getStatusCode();

        return ResponseEntity
                .status(statusCode)
                .contentType(mediaType)
                .body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTaskException(TaskException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler
    public ResponseEntity<MultiLanguageFieldDto> handleLoginException(LoginException ex) {
        MultiLanguageFieldDto errorMessage = MultiLanguageFieldDto.builder()
                .en("Invalid email or password")
                .uk("Неправильний імейл або пароль")
                .build();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTokenException(TokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTaskAssignmentException(TaskAssignmentException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(FileException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<Object>> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldKey = fieldError.getField() + "Errors";

            Object rejectedValue = fieldError.getRejectedValue();
            if (rejectedValue instanceof MultiLanguageFieldDto) {
                errors.computeIfAbsent(fieldKey, k -> new ArrayList<>()).add(rejectedValue);
            } else {
                Object multiLangError = buildMultiLanguageError(fieldError);
                errors.computeIfAbsent(fieldKey, k -> new ArrayList<>()).add(multiLangError);
            }
        }

        for (ObjectError globalError : ex.getBindingResult().getGlobalErrors()) {
            String objectKey = globalError.getObjectName() + "Errors";
            Object multiLangError = buildMultiLanguageError(globalError);
            errors.computeIfAbsent(objectKey, k -> new ArrayList<>()).add(multiLangError);
        }

        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler
    public String handleRecruitmentException(RecruitmentException recruitmentException) {
        return recruitmentException.getMessage();
    }

    @ExceptionHandler
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadCredentialsException(AccountAuthException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
    }

    private Object buildMultiLanguageError(ObjectError error) {
        Locale ukrainianLocale = new Locale("uk", "UA");
        Locale englishLocale = Locale.ENGLISH;
        String messageUk;
        String messageEn;
        String defaultMessage = error.getDefaultMessage();

        if (error instanceof FieldError fieldError) {
            boolean isExpertiseFieldError = "expertise".equals(fieldError.getField())
                    && "typeMismatch".equals(fieldError.getCode());
            if (isExpertiseFieldError) {
                return buildExpertiseError(ukrainianLocale, englishLocale);
            }
        }

        if (StringUtils.isNotEmpty(defaultMessage)) {
            try {
                messageEn = messageSource.getMessage(defaultMessage, null, Locale.ENGLISH);
                messageUk = messageSource.getMessage(defaultMessage, null, ukrainianLocale);
            } catch (NoSuchMessageException ex) {
                return defaultMessage;
            }
        } else {
            throw new RuntimeException("Error message is empty");
        }

        return MultiLanguageFieldDto.builder()
                .en(messageEn)
                .uk(messageUk)
                .build();
    }

    private Object buildExpertiseError(Locale ukLocale, Locale enLocale) {
        String errorMessageCode = "registration.validation.expertise.allowedValues";

        return MultiLanguageFieldDto.builder()
                .en(messageSource.getMessage(errorMessageCode, null, enLocale))
                .uk(messageSource.getMessage(errorMessageCode, null, ukLocale))
                .build();
    }

}
