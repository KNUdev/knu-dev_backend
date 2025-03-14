package ua.knu.knudev.assessmentmanagerapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestScore {
    private double rawScore;
    private double percentageScore;
}
