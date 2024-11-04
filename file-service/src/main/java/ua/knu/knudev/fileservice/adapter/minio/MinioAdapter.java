package ua.knu.knudev.fileservice.adapter.minio;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.subfolder.FileSubfolder;

@Service
@RequiredArgsConstructor
public class MinioAdapter implements FileUploadAdapter {

    private final MinioClient minioClient;

    @Override
    public String saveFile(FileUploadPayload payload) {
        createBucket(payload.folderPath().path());
        saveImage(payload);
        return payload.fileName();
    }

    @Override
    @SneakyThrows
    public boolean existsByFilename(String filename, FileFolderProperties<? extends FileSubfolder> fileFolderProperties) {
        String filePath = fileFolderProperties.getSubfolder().getSubfolderPath() + "/" + filename;

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(fileFolderProperties.getFolder().getName())
                    .object(filePath).build());
            return true;
        } catch (MinioException e) {
            return false;
        }
    }

    @SneakyThrows
    void createBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    @SneakyThrows
    boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
    }

    @SneakyThrows
    void saveImage(FileUploadPayload payload) {
        String filePath = payload.folderPath().subfolderPath() + "/" + payload.fileName();

        minioClient.putObject(PutObjectArgs.builder()
                .stream(payload.inputStream(), payload.inputStream().available(), -1)
                .bucket(payload.folderPath().path())
                .object(filePath)
                .build());
    }
}
