//package ua.knu.knudev.intergrationtests;
//
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.multipart.MultipartFile;
//import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
//import ua.knu.knudev.educationapi.request.TopicCreationRequest;
//import ua.knu.knudev.educationapi.request.SectionCreationRequest;
//import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
//import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;
//import ua.knu.knudev.knudevcommon.constant.Expertise;
//import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
//
//import java.util.Set;
//import java.util.UUID;
//
//@SpringBootTest(classes = IntegrationTestsConfig.class)
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class EducationProgramCreationServiceTest {
//
//    private MultipartFile getMockMultipartFile() {
//        return new MockMultipartFile(
//                "avatarFile",
//                "avatar.png",
//                "image/png",
//                "dummy content".getBytes()
//        );
//    }
//
//    void should_Successfully_CreateNewProgram_When_GivenValidRequest() {
//        TopicCreationRequest topic1 = new TopicCreationRequest(
//                new MultiLanguageFieldDto("English Test Name1", "Українське тестове ім'я1"),
//                new MultiLanguageFieldDto("English Test Name1", "Українське тестове ім'я1"),
//                getMockMultipartFile(), Set.of("https://example.com/1", "https://example.com/2"),
//                1, Set.of(UUID.randomUUID(), UUID.randomUUID())
//        );
//        TopicCreationRequest topic2 = new TopicCreationRequest(
//                new MultiLanguageFieldDto("English Test Name2", "Українське тестове ім'я2"),
//                new MultiLanguageFieldDto("English Test Name2", "Українське тестове ім'я2"),
//                getMockMultipartFile(), Set.of("https://example.com/1", "https://example.com/2"),
//                1, Set.of(UUID.randomUUID(), UUID.randomUUID())
//        );
//        ModuleCreationRequest module1 = new ModuleCreationRequest(
//                new MultiLanguageFieldDto("English Test Name2", "Українське тестове ім'я2"),
//                new MultiLanguageFieldDto("English Test Name2", "Українське тестове ім'я2"),
//                Set.of(topic1, topic2)
//        );
//
//        SectionCreationRequest s1 = new SectionCreationRequest()
//
//
//        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
//                new MultiLanguageFieldDto("English Test Name", "Українське тестове ім'я"),
//                new MultiLanguageFieldDto("English Test Name", "Українське тестове ім'я"),
//                Set.of(
//
//                ),
//                Expertise.BACKEND,
//                getMockMultipartFile()
//        );
//
//    }
//
//
//}
