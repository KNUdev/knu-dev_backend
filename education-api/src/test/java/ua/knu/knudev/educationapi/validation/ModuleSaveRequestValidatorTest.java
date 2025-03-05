package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModuleSaveRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void should_ValidateSuccessfully_When_ExistingModuleIdAndOrderIndexAreProvided() {
        ModuleSaveRequest request = ModuleSaveRequest.builder()
                .existingModuleId(UUID.randomUUID())
                .orderIndex(1)
                .build();

        Set<ConstraintViolation<ModuleSaveRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_ExistingModuleIdAndOtherFieldsAreProvided() {
        ModuleSaveRequest request = ModuleSaveRequest.builder()
                .existingModuleId(UUID.randomUUID())
                .orderIndex(1)
                .name(new MultiLanguageFieldDto())
                .build();

        Set<ConstraintViolation<ModuleSaveRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }

    @Test
    public void should_ValidateSuccessfully_When_AllFieldsExceptExistingModuleIdAreProvided() {
        ModuleSaveRequest request = ModuleSaveRequest.builder()
                .name(new MultiLanguageFieldDto())
                .description(new MultiLanguageFieldDto())
                .topics(Collections.emptyList())
                .finalTask(new MockMultipartFile(
                        "file",
                        "filename.txt",
                        "text/plain",
                        "content".getBytes()
                ))
                .orderIndex(1)
                .build();

        Set<ConstraintViolation<ModuleSaveRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_NoExistingIdOrAllOtherFieldsAreProvided() {
        ModuleSaveRequest request = ModuleSaveRequest.builder()
                .name(new MultiLanguageFieldDto())
                .orderIndex(1)
                .build();

        Set<ConstraintViolation<ModuleSaveRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }
}
