package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;

public class ModuleCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, ModuleCreationRequest> {

    @Override
    public boolean isValid(ModuleCreationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingModuleId() != null;
        boolean hasAllFields = request.getName() != null
                && request.getDescription() != null
                && request.getTopics() != null
                && request.getFinalTask() != null
                && request.getTestId() != null;

        if (hasExistingId) {
            boolean onlyExistingAndOrder = request.getName() == null
                    && request.getDescription() == null
                    && request.getTopics() == null
                    && request.getFinalTask() == null
                    && request.getTestId() == null;
            if (!onlyExistingAndOrder) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "When existingModuleId is provided, " +
                                        "only existingModuleId and orderIndex must be present")
                        .addPropertyNode("existingModuleId")
                        .addConstraintViolation();

                return false;
            }
        } else {
            if (!hasAllFields) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "All fields except existingModuleId must be provided"
                ).addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
