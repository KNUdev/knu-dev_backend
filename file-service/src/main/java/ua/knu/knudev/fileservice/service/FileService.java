package ua.knu.knudev.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;
import ua.knu.knudev.fileserviceapi.dto.FolderPath;
import ua.knu.knudev.fileserviceapi.exception.FileException;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService implements FileServiceApi {

    private static final String FILE_EXTENSION_SEPARATOR = ".";
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
        validateFileExtension(extension);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename) || !StringUtils.contains(originalFilename, ".")) {
            throw new FileException("Invalid file name: no extension found.");
        }

        int fileExtensionIndex = originalFilename.lastIndexOf(FILE_EXTENSION_SEPARATOR) + 1;
        return originalFilename.substring(fileExtensionIndex);
    }

    private void validateFileExtension(String extension) {
        if (extension.matches(".*[<>:\"/\\\\|?*].*") || extension.contains("..")) {
            throw new FileException("Invalid file extension.");
        }
    }

}
