package ua.knu.knudev.educationapi.dto.summary;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
class ModuleSummaryDto {
    private UUID moduleId;
    private String moduleName;
    private List<TopicSummaryDto> topics;
}