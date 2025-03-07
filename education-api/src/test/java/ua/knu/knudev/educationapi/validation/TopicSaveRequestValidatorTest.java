package ua.knu.knudev.educationapi.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopicSaveRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void should_ValidateSuccessfully_When_ExistingTopicIdAndOrderIndexAreProvided() {
        TopicSaveRequest request = TopicSaveRequest.builder()
                .existingTopicId(UUID.randomUUID())
                .orderIndex(1)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_ExistingTopicIdAndOtherFieldsAreProvided() {
        TopicSaveRequest request = TopicSaveRequest.builder()
                .existingTopicId(UUID.randomUUID())
                .orderIndex(1)
                .name(new MultiLanguageFieldDto())
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }

    @Test
    public void should_ValidateSuccessfully_When_AllFieldsExceptExistingTopicIdAreProvided() {
        TopicSaveRequest request = TopicSaveRequest.builder()
                .name(new MultiLanguageFieldDto())
                .description(new MultiLanguageFieldDto())
                .finalTask(new MockMultipartFile(
                        "file",
                        "filename.txt",
                        "text/plain",
                        "content".getBytes()
                ))
                .learningResources(List.of("Material"))
                .orderIndex(1)
                .testId(UUID.randomUUID())
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_NoExistingIdOrAllOtherFieldsAreProvided() {
        TopicSaveRequest request = TopicSaveRequest.builder()
                .name(new MultiLanguageFieldDto())
                .orderIndex(1)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }
}
