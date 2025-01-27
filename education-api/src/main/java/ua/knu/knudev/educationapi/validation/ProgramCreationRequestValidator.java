package ua.knu.knudev.educationapi.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

public class ProgramCreationRequestValidator implements ConstraintValidator<ValidCreationRequest, EducationProgramCreationRequest> {

    @Override
    public boolean isValid(EducationProgramCreationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasExistingId = request.getExistingProgramId() != null;
        boolean hasAllFields = request.getName() != null
                && request.getDescription() != null
                && request.getSections() != null
                && request.getExpertise() != null
                && request.getFinalTask() != null;

        if (hasExistingId) {
            boolean onlyExistingAndOrder = request.getName() == null
                    && request.getDescription() == null
                    && request.getSections() == null
                    && request.getExpertise() == null
                    && request.getFinalTask() == null;
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
