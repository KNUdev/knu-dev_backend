package ua.knu.knudev.fileservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.ImageFolder;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.knudevcommon.exception.FileException;

import java.io.IOException;
import java.util.Set;

@Service
public class ImageService extends FileService implements ImageServiceApi {
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    @Value("${application.files.images.maximum-size-in-kilobytes}")
    private int MAX_IMAGE_SIZE_IN_KILOBYTES;

    public ImageService(FileUploadAdapter fileUploadAdapter) {
        super(fileUploadAdapter);
    }

    @Override
    public String uploadFile(MultipartFile file, String filename, ImageSubfolder subfolder) {
        checkFileValidity(file);
        return uploadFile(file, filename, getProperties(subfolder));
    }

    @Override
    public String uploadFile(MultipartFile file, ImageSubfolder subfolder) {
        checkFileValidity(file);

        String filename = generateRandomUUIDFilename(file);
        return uploadFile(file, filename, getProperties(subfolder));
    }

    @Override
    public boolean existsByFilename(String filename, ImageSubfolder subfolder) {
        return existsByFilename(filename, getProperties(subfolder));
    }

    private void checkFileValidity(MultipartFile file) {
        checkFileExtensionAllowance(file, ALLOWED_IMAGE_EXTENSIONS);
        checkFileSize(file);
    }

    private FileFolderProperties<ImageSubfolder> getProperties(ImageSubfolder imageSubfolder) {
        return FileFolderProperties.builder(ImageFolder.INSTANCE)
                .subfolder(imageSubfolder)
                .build();
    }

    private void checkFileSize(MultipartFile file) {
        try {
            int fileSizeInMegabytes = file.getBytes().length / 1024 / 1024;
            if (fileSizeInMegabytes > MAX_IMAGE_SIZE_IN_KILOBYTES) {
                throw new FileException("File size is too big. Maximum allowed size is 2 megabytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
