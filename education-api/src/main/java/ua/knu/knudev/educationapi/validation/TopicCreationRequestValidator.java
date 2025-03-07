package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;

public class TopicCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, TopicSaveRequest> {

    @Override
    public boolean isValid(TopicSaveRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingTopicId() != null;
        boolean hasAllFields = ObjectUtils.anyNotNull(
                request.getName(),
                request.getDescription(),
                request.getFinalTask(),
                request.getLearningResources(),
                request.getDifficulty()
        );

        if (hasExistingId) {
            boolean onlyExistingAndOrder = ObjectUtils.allNull(
                    request.getName(),
                    request.getDescription(),
                    request.getFinalTask(),
                    request.getLearningResources(),
                    request.getTestId(),
                    request.getDifficulty()
            );

            if (!onlyExistingAndOrder) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "When existingTopicId is provided, " +
                                        "only existingTopicId and orderIndex must be present"
                        )
                        .addPropertyNode("existingTopicId")
                        .addConstraintViolation();

                return false;
            }
        } else {
            if (!hasAllFields) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "All fields except existingTopicId must be provided"
                ).addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
