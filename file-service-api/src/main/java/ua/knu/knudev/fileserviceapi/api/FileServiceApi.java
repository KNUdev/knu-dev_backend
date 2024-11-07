package ua.knu.knudev.fileserviceapi.api;

import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.ImageFolder;
import ua.knu.knudev.fileserviceapi.folder.PdfFolder;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

public interface FileServiceApi {
    String uploadFile(MultipartFile file, FileFolderProperties<? extends FileSubfolder> fileFolderProperties);

    default String uploadAccountPicture(MultipartFile file) {
        FileFolderProperties<ImageSubfolder> properties = FileFolderProperties.builder(ImageFolder.INSTANCE)
                .subfolder(ImageSubfolder.ACCOUNT_PICTURES)
                .build();
        return uploadFile(file, properties);
    }

    default String uploadPdf(MultipartFile file) {
        FileFolderProperties<PdfSubfolder> properties = FileFolderProperties.builder(PdfFolder.INSTANCE)
//                .subfolder(PdfSubfolder.TASK_BODIES)
                .build();
        return uploadFile(file, properties);
    }
}
