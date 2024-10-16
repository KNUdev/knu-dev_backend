package ua.knu.knudev.fileserviceapi.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class FileUploadPayload {
    private InputStream inputStream;
    private String fileName;
    private FolderPath folderPath;
}
