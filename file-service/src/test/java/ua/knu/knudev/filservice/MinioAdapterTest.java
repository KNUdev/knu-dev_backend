package ua.knu.knudev.filservice;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.fileservice.adapter.minio.MinioAdapter;
import ua.knu.knudev.fileserviceapi.dto.FileUploadPayload;
import ua.knu.knudev.fileserviceapi.dto.FolderPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioAdapterTest {
    private final String BUCKET_NAME = "test-folder";
    private final String SUBFOLDER_PATH = "test-subfolder";
    private final String RANDOM_FILENAME = UUID.randomUUID() + ".pdf";

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioAdapter minioAdapter;
    private FileUploadPayload fileUploadPayload;

    @BeforeEach
    void setUp() {
        InputStream inputStream = new ByteArrayInputStream("dummy content".getBytes());
        FolderPath folderPath = FolderPath.builder().path(BUCKET_NAME).subfolderPath(SUBFOLDER_PATH).build();

        fileUploadPayload = FileUploadPayload.builder()
                .inputStream(inputStream)
                .fileName(RANDOM_FILENAME)
                .folderPath(folderPath)
                .build();
    }

    @Test
    void should_CreateBucketIfNotExists_When_BucketIsCreated() throws Exception {
        when(minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())).thenReturn(false);

        minioAdapter.saveFile(fileUploadPayload);

        verify(minioClient).makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
    }

    @Test
    void should_NotCreateBucket_When_BucketIsAlreadyCreated() {
        //todo create


    }

    @Test
    void should_SaveFileWithCorrectParameters_When_FileIsSaved() throws Exception {
        when(minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())).thenReturn(true);

        minioAdapter.saveFile(fileUploadPayload);

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());
        PutObjectArgs actualArgs = captor.getValue();

        assertEquals(BUCKET_NAME, actualArgs.bucket(), "Bucket name should be correct.");
        assertEquals(SUBFOLDER_PATH + "/" + RANDOM_FILENAME, actualArgs.object(),
                "Object path should be correct."
        );
    }
}
