package ua.knu.knudev.fileserviceapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;

public interface BaseFileServiceApi<T extends FileSubfolder> {
    String uploadFile(MultipartFile file, String filename, T subfolder);
    String uploadFile(MultipartFile file, T subfolder);

    boolean existsByFilename(String filename, T subfolder);
}
