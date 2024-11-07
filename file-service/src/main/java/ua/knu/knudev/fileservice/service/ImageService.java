package ua.knu.knudev.fileservice.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.exception.FileException;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.ImageFolder;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;

import java.io.IOException;
import java.util.Set;

@Service
public class ImageService extends FileService implements ImageServiceApi {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final int MAXIMUM_ALLOWED_IMAGE_SIZE_IN_MEGABYTES = 2;

    public ImageService(FileUploadAdapter fileUploadAdapter) {
        super(fileUploadAdapter);
    }

    @Override
    public String uploadFile(MultipartFile file, ImageSubfolder subfolder) {
        checkFileExtensionAllowance(file, ALLOWED_IMAGE_EXTENSIONS);
        checkFileSize(file);

        return uploadFile(file, getProperties(subfolder));
    }

    @Override
    public boolean existsByFilename(String filename, ImageSubfolder subfolder) {
        return existsByFilename(filename, getProperties(subfolder));
    }

    private FileFolderProperties<ImageSubfolder> getProperties(ImageSubfolder imageSubfolder) {
        return FileFolderProperties.builder(ImageFolder.INSTANCE)
                .subfolder(imageSubfolder)
                .build();
    }


    private void checkFileSize(MultipartFile file) {
        try {
            int fileSizeInMegabytes = file.getBytes().length / 1024 / 1024;
            if(fileSizeInMegabytes > MAXIMUM_ALLOWED_IMAGE_SIZE_IN_MEGABYTES) {
                throw new FileException("File size is too big. Maximum allowed size is 2 megabytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
