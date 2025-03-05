package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;

//todo refactor
public class ModuleCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, ModuleSaveRequest> {

    @Override
    public boolean isValid(ModuleSaveRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingModuleId() != null;
        boolean hasAllFields = ObjectUtils.allNotNull(
                request.getName(),
                request.getDescription(),
//                request.getTopics(),
                request.getFinalTask()
        );

        if (hasExistingId) {
            boolean onlyExistingAndOrder = ObjectUtils.allNull(
                    request.getName(),
                    request.getDescription(),
//                    request.getTopics(),
                    request.getFinalTask()
            );

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
