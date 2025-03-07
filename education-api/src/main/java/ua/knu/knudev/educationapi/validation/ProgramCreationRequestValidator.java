package ua.knu.knudev.educationapi.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.ObjectUtils;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;

public class ProgramCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, ProgramSaveRequest> {

    @Override
    public boolean isValid(ProgramSaveRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingProgramId() != null;
        boolean hasAllFields = ObjectUtils.anyNotNull(
                request.getName(),
                request.getDescription(),
                request.getExpertise(),
                request.getFinalTask()
        );

        if (hasExistingId) {
            boolean onlyExistingAndOrder = ObjectUtils.allNull(
                    request.getName(),
                    request.getDescription(),
                    request.getExpertise(),
                    request.getFinalTask()
            );

            if (!onlyExistingAndOrder) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "When existingProgramId is provided, other fields must be null"
                ).addConstraintViolation();

                return false;
            }
        } else {
            if (!hasAllFields) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "All fields except existingProgramId must be provided"
                ).addConstraintViolation();

                return false;
            }
        }

        return true;
    }
}
