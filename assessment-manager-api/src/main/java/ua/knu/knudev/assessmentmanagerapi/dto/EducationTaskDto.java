package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EducationTaskDto {
    private UUID id;
    private String filename;
}
