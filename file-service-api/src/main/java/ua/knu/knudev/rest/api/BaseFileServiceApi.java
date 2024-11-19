package ua.knu.knudev.rest.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.rest.subfolder.FileSubfolder;

public interface BaseFileServiceApi<T extends FileSubfolder> {
    String uploadFile(MultipartFile file, String filename, T subfolder);
    String uploadFile(MultipartFile file, T subfolder);

    boolean existsByFilename(String filename, T subfolder);
}
