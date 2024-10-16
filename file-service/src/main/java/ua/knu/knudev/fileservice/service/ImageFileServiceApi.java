package ua.knu.knudev.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.fileserviceapi.dto.FolderPath;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.exception.FileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileServiceApi implements FileServiceApi {
    private final FileUploadAdapter fileUploadAdapter;

    @Override
    public String uploadFile(MultipartFile file, FileFolderProperties<? extends FileSubfolder> fileFolderProperties) {
        String folderName = fileFolderProperties.getFolder().getName();
        String subfolderPath = fileFolderProperties.getSubfolder().getSubfolderPath();

        FileUploadPayload fileUploadPayload = FileUploadPayload.builder()
                .inputStream(getInputStream(file))
                .fileName(generateFileName(file))
                .folderPath(FolderPath.builder()
                        .subfolderPath(subfolderPath)
                        .path(folderName)
                        .build())
                .build();

        return fileUploadAdapter.saveFile(fileUploadPayload);
    }

    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new FileException("Could not get input stream, because file is corrupted");
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        return file.getOriginalFilename()
                .substring(file.getOriginalFilename()
                        .lastIndexOf(".") + 1);
    }

}
