package ua.knu.knudev.assessmentmanagerapi.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SubmittedAnswerDto {
    private UUID questionId;
    private List<UUID> chosenVariantIds;
}
