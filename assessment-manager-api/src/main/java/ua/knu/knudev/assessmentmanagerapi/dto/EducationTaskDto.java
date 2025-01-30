package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EducationTaskDto {
    private UUID id;
    private String filename;
}
