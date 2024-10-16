package ua.knu.knudev.fileserviceapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;

public interface FileServiceApi {
    String uploadFile(MultipartFile file, FileFolderProperties<? extends FileSubfolder> fileFolderProperties);
}
