package ua.knu.knudev.fileserviceapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderPath {
    private String path;
    private String subfolderPath;
}
