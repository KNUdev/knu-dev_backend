package ua.knu.knudev.fileserviceapi.subfolder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PdfSubfolder implements FileSubfolder {
    REQUIREMENTS("/requirements"),
    TASK_BODIES("/tasks/bodies");

    private final String subfolderPath;

    @Override
    public String getSubfolderPath() {
        return subfolderPath;
    }
}