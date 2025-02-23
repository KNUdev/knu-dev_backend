package ua.knu.knudev.teammanager.github.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GithubRepoDataDto(
    String name,
    LocalDate initializedAt,
    LocalDateTime lastUpdatedAt,
    List<String> contributors,
    String resourceUrl
){
}
