package ua.knu.knudev.educationapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TestMetadataDto {
    private UUID testId;
    private String testName;
    private int totalQuestions;
    // any additional test fields…
}

