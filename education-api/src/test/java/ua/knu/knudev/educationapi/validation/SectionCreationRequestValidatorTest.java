package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Collections;
import java.util.UUID;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SectionCreationRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void should_ValidateSuccessfully_When_ExistingSectionIdAndOrderIndexAreProvided() {
        SectionCreationRequest request = SectionCreationRequest.builder()
                .existingSectionId(UUID.randomUUID())
                .orderIndex(1)
                .build();

        Set<ConstraintViolation<SectionCreationRequest>> violations = validator.validate(request);
        assertTrue(
                violations.isEmpty(),
                "Expected no validation errors when existingSectionId and orderIndex are provided alone."
        );
    }

    @Test
    public void should_NotValidate_When_ExistingSectionIdAndOtherFieldsAreProvided() {
        SectionCreationRequest request = SectionCreationRequest.builder()
                .existingSectionId(UUID.randomUUID())
                .orderIndex(1)
                .name(new MultiLanguageFieldDto())
                .description(new MultiLanguageFieldDto())
                .modules(Collections.emptyList())
                .finalTask(new MockMultipartFile(
                        "file",
                        "filename.txt",
                        "text/plain",
                        "content".getBytes()
                ))
                .build();

        Set<ConstraintViolation<SectionCreationRequest>> violations = validator.validate(request);
        assertFalse(
                violations.isEmpty(),
                "Expected validation errors when existingSectionId is provided along with other fields."
        );
    }

    @Test
    public void should_ValidateSuccessfully_When_AllFieldsExceptExistingSectionIdAreProvided() {
        SectionCreationRequest request = SectionCreationRequest.builder()
                .name(new MultiLanguageFieldDto())
                .description(new MultiLanguageFieldDto())
                .modules(Collections.emptyList())
                .finalTask(new MockMultipartFile(
                        "file",
                        "filename.txt",
                        "text/plain",
                        "content".getBytes()
                ))
                .orderIndex(2)
                .build();

        Set<ConstraintViolation<SectionCreationRequest>> violations = validator.validate(request);
        assertTrue(
                violations.isEmpty(),
                "Expected no validation errors when all required fields are provided without existingSectionId."
        );
    }

    @Test
    public void should_NotValidate_When_NoExistingIdOrAllOtherFieldsAreProvided() {
        SectionCreationRequest request = SectionCreationRequest.builder()
                .name(new MultiLanguageFieldDto())
                .orderIndex(3)
                .build();

        Set<ConstraintViolation<SectionCreationRequest>> violations = validator.validate(request);
        assertFalse(
                violations.isEmpty(),
                "Expected validation errors when required fields are missing without existingSectionId."
        );
    }

}
