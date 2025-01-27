package ua.knu.knudev.educationapi.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopicCreationRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void should_ValidateSuccessfully_When_ExistingTopicIdAndOrderIndexAreProvided() {
        TopicCreationRequest request = TopicCreationRequest.builder()
                .existingTopicId(UUID.randomUUID())
                .orderIndex(1)
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_ExistingTopicIdAndOtherFieldsAreProvided() {
        TopicCreationRequest request = TopicCreationRequest.builder()
                .existingTopicId(UUID.randomUUID())
                .orderIndex(1)
                .name(new MultiLanguageFieldDto())
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }

    @Test
    public void should_ValidateSuccessfully_When_AllFieldsExceptExistingTopicIdAreProvided() {
        TopicCreationRequest request = TopicCreationRequest.builder()
                .name(new MultiLanguageFieldDto())
                .description(new MultiLanguageFieldDto())
                .task(new MockMultipartFile(
                        "file",
                        "filename.txt",
                        "text/plain",
                        "content".getBytes()
                ))
                .learningMaterials(Collections.singleton("Material"))
                .orderIndex(1)
                .testIds(Collections.singleton(UUID.randomUUID()))
                .build();

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void should_NotValidate_When_NoExistingIdOrAllOtherFieldsAreProvided() {
        TopicCreationRequest request = TopicCreationRequest.builder()
                .name(new MultiLanguageFieldDto())
                .orderIndex(1)
                .build();

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected validation errors");
    }
}
