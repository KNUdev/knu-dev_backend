//package ua.knu.knudev.filservice;
//
//import io.minio.MinioClient;
//import io.minio.PutObjectArgs;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//import ua.knu.knudev.fileservice.test.adapter.minio.MinioBuckets;
//import ua.knu.knudev.fileservice.test.adapter.minio.MinioProperties;
//import ua.knu.knudev.fileservice.service.FileService;
//import ua.knu.knudev.fileserviceapi.exception.FileException;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.regex.Pattern;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class FileServiceTest {
//
//    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
//    private static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);
//    private static final String FILE_EXTENSION = "pdf";
//    private static final String TEST_BUCKET_NAME = "test-bucket";
//
//    @Mock
//    private MinioClient minioClient;
//
//    @Mock
//    private MultipartFile multipartFile;
//
//    @Mock
//    private MinioBuckets minioBuckets;
//
//    @InjectMocks
//    private FileService fileService;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
//        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));
//        when(minioProperties.getBucket()).thenReturn(TEST_BUCKET_NAME);
//    }
//
//    @Test
//    @DisplayName("Should return UUID filename when file is uploaded")
//    void should_ReturnUUIDFilename_When_FileIsUploaded() {
//        String result = uploadFile();
//
//        assertNotNull(result, "The generated file name should not be null.");
//        assertValidUUIDFileName(result);
//    }
//
//    @Test
//    @DisplayName("Should use correct bucket and filename when uploading file")
//    void should_UseCorrectBucketName_When_UploadingFile() {
//        uploadFile();
//
//        PutObjectArgs actualArgs = verifyPutObjectInvocation();
//        assertEquals(TEST_BUCKET_NAME, actualArgs.bucket(), "Bucket name should be 'test-bucket'.");
//    }
//
//    @Test
//    @DisplayName("Should save file with correct content when uploading file")
//    void should_SaveFileWithCorrectContent_When_UploadingFile() throws Exception {
//        uploadFile();
//
//        PutObjectArgs actualArgs = verifyPutObjectInvocation();
//        InputStream inputStream = actualArgs.stream();
//        byte[] contentBytes = inputStream.readAllBytes();
//
//        assertNotNull(contentBytes, "Input stream content should not be null.");
//        assertArrayEquals("dummy content".getBytes(), contentBytes, "Input stream content should match.");
//    }
//
//    @Test
//    @DisplayName("Should throw exception when multipart file is corrupted")
//    void should_ThrowException_When_MultipartFileIsCorrupted() throws IOException {
//        when(multipartFile.getInputStream()).thenThrow(new FileException("Test file corrupted exception"));
//
//        assertThrows(FileException.class, () -> fileService.uploadFile(multipartFile),
//                "Expected FileException when input stream throws IOException.");
//    }
//
//    private String uploadFile() {
//        return fileService.uploadFile(multipartFile);
//    }
//
//    @SneakyThrows
//    private PutObjectArgs verifyPutObjectInvocation() {
//        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
//        verify(minioClient).putObject(captor.capture());
//        return captor.getValue();
//    }
//
//    private void assertValidUUIDFileName(String fileName) {
//        assertTrue(
//                UUID_PATTERN.matcher(fileName.split("\\.")[0]).matches(),
//                "The generated file name should contain a valid UUID."
//        );
//        assertTrue(fileName.endsWith(FILE_EXTENSION), "The generated file name should have the correct file extension.");
//    }
//}
