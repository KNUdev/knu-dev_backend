package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;

public class TopicCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, TopicCreationRequest> {

    @Override
    public boolean isValid(TopicCreationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingTopicId() != null;
        boolean hasAllFields = request.getName() != null
                && request.getDescription() != null
                && request.getTask() != null
                && request.getLearningMaterials() != null
                && request.getTestIds() != null;

        if (hasExistingId) {
            boolean onlyExistingAndOrder = request.getName() == null
                    && request.getDescription() == null
                    && request.getTask() == null
                    && request.getLearningMaterials() == null
                    && request.getTestIds() == null;
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
