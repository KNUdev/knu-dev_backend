package ua.knu.knudev.educationapi.dto.summary;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProgramSummaryDto {
    private UUID programId;
    private String programName;
    private List<SectionSummaryDto> sections;
}