package ua.knu.knudev.rest.dto;

import lombok.Builder;

@Builder
public record FolderPath(String path, String subfolderPath) {
}
