package ua.knu.knudev.educationapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;

//todo refactor
public class SectionCreationRequestValidator
        implements ConstraintValidator<ValidCreationRequest, SectionSaveRequest> {

    @Override
    public boolean isValid(SectionSaveRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingSectionId() != null;
        boolean hasAllFields = ObjectUtils.allNotNull(
                request.getName(),
                request.getDescription(),
                request.getFinalTask()
        );

        if (hasExistingId) {
            boolean onlyExistingAndOrder = ObjectUtils.allNull(
                    request.getName(),
                    request.getDescription(),
                    request.getFinalTask()
            );

            if (!onlyExistingAndOrder) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "When existingSectionId is provided, other fields must be null"
                ).addConstraintViolation();
                return false;
            }
        } else {
            if (!hasAllFields) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "All fields except existingSectionId must be provided"
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
