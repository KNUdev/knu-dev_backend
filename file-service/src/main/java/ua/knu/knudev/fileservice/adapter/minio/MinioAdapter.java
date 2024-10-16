package ua.knu.knudev.fileservice.adapter.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;

@Service
@RequiredArgsConstructor
//todo exc handling
public class MinioAdapter implements FileUploadAdapter {

    private final MinioClient minioClient;

    @Override
    public String saveFile(FileUploadPayload payload) {
        createBucket(payload.getFolderPath().getPath());
        saveImage(payload);
        return payload.getFileName();
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
        String filePath = payload.getFolderPath().getSubfolderPath() + "/" + payload.getFileName();

        minioClient.putObject(PutObjectArgs.builder()
                .stream(payload.getInputStream(), payload.getInputStream().available(), -1)
                .bucket(payload.getFolderPath().getPath())
                .object(filePath)
                .build());
    }
}
