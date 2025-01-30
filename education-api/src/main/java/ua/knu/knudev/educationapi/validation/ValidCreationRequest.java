package ua.knu.knudev.educationapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = {
        ProgramCreationRequestValidator.class,
        SectionCreationRequestValidator.class,
        ModuleCreationRequestValidator.class,
        TopicCreationRequestValidator.class,
})
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreationRequest {
    String message() default "Invalid creation request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}