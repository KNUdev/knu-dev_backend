package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AnswerVariantResultDto {
    private UUID variantId;
    private String variantBody;
    private boolean selectedByUser;
    private boolean correct;
}
