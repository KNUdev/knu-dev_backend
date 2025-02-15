package ua.knu.knudev.educationapi.dto.summary;

import lombok.Data;

import java.util.UUID;

@Data
class TopicSummaryDto {
    private UUID topicId;
    private String topicName;
}
