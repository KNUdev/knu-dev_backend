package ua.knu.knudev.fileservice.adapter;

import ua.knu.knudev.rest.dto.FileUploadPayload;
import ua.knu.knudev.rest.folder.FileFolderProperties;
import ua.knu.knudev.rest.subfolder.FileSubfolder;

public interface FileUploadAdapter {
    String saveFile(FileUploadPayload payload);

    boolean existsByFilename(String filename, FileFolderProperties<? extends FileSubfolder> fileFolderProperties);
}
