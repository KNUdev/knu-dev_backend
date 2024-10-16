package ua.knu.knudev.fileservice.adapter;

import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;

public interface FileUploadAdapter{
    String saveFile(FileUploadPayload payload);
}
