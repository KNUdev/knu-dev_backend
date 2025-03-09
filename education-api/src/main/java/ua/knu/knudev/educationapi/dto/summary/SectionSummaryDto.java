package ua.knu.knudev.educationapi.dto.summary;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
class SectionSummaryDto {
    private UUID sectionId;
    private String sectionName;
    private List<ModuleSummaryDto> modules;
}
