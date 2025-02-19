package ua.knu.knudev.teammanager.github.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GitHubRepoDataDto (
    String name,
    LocalDate startedAt,
    LocalDateTime lastUpdatedAt,
    List<String> contributors,
    String resourceUrl
){
}
