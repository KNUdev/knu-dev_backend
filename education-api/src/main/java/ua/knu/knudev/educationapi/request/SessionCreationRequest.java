package ua.knu.knudev.educationapi.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionCreationRequest(
        UUID educationProgramId,
        LocalDateTime startDate
) {

}
