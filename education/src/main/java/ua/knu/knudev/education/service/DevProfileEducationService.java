package ua.knu.knudev.education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.educationapi.api.DevProfileEducationApi;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class DevProfileEducationService implements DevProfileEducationApi {
    private final EducationProgramApi programApi;
    private MultiLanguageFieldDto multiLangField = MultiLanguageFieldDto.builder()
            .uk("Тестове створення")
            .en("Test creation")
            .build();

    @Override
    public EducationProgramDto createTestProgram() {
        ProgramSaveRequest saveRequest = ProgramSaveRequest.builder()
                .name(multiLangField)
                .description(multiLangField)
                .expertise(Expertise.BACKEND)
                .sections(getSectionSaveRequests(10))
                .finalTask(createPdfMultipartFile())
                .build();
        return programApi.save(saveRequest);
    }

    private List<TopicSaveRequest> getTopicSaveRequests(int size) {
        List<TopicSaveRequest> topicSaveRequests = new ArrayList<>();
        List<String> learningResources = List.of(
                "https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/",
                "https://vladmihalcea.com/courses/high-performance-java-persistence/",
                "https://www.baeldung.com/hibernate-lazycollection",
                "https://vladmihalcea.com/courses/"
        );
        for (int i = 1; i < size; i++) {
            TopicSaveRequest topicSaveRequest = TopicSaveRequest.builder()
                    .name(multiLangField)
                    .orderIndex(i)
                    .description(multiLangField)
                    .difficulty(10)
                    .learningResources(learningResources)
                    .finalTask(createPdfMultipartFile())
                    .build();
            topicSaveRequests.add(topicSaveRequest);
        }
        return topicSaveRequests;
    }

    private List<ModuleSaveRequest> getModuleSaveRequests(int size) {
        List<ModuleSaveRequest> moduleSaveRequests = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            ModuleSaveRequest moduleSaveRequest = ModuleSaveRequest.builder()
                    .name(multiLangField)
                    .orderIndex(i)
                    .description(multiLangField)
                    .topics(getTopicSaveRequests(10))
                    .finalTask(createPdfMultipartFile())
                    .build();
            moduleSaveRequests.add(moduleSaveRequest);
        }
        return moduleSaveRequests;
    }

    private List<SectionSaveRequest> getSectionSaveRequests(int size) {
        List<SectionSaveRequest> sectionSaveRequests = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            SectionSaveRequest saveSaveRequest = SectionSaveRequest.builder()
                    .name(multiLangField)
                    .orderIndex(i)
                    .description(multiLangField)
                    .modules(getModuleSaveRequests(10))
                    .finalTask(createPdfMultipartFile())
                    .build();
            sectionSaveRequests.add(saveSaveRequest);
        }
        return sectionSaveRequests;
    }

    public static MultipartFile createPdfMultipartFile() {
        String minimalPdf =
                "%PDF-1.4\n" +
                        "1 0 obj\n<<>>\nendobj\n" +
                        "xref\n0 1\n0000000000 65535 f \n" +
                        "trailer\n<< /Root 1 0 R /Size 1 >>\n" +
                        "startxref\n9\n%%EOF\n";

        byte[] pdfBytes = minimalPdf.getBytes(StandardCharsets.US_ASCII);
        return new InMemoryMultipartFile(
                pdfBytes,
                "file",
                "test.pdf",
                "application/pdf"
        );
    }

    private static class InMemoryMultipartFile implements MultipartFile {

        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public InMemoryMultipartFile(byte[] content,
                                     String name,
                                     String originalFilename,
                                     String contentType) {
            this.content = (content != null ? content : new byte[0]);
            this.name = (name != null ? name : "");
            this.originalFilename = (originalFilename != null ? originalFilename : "");
            this.contentType = (contentType != null ? contentType : "");
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return this.content.length == 0;
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return this.content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.content, dest);
        }
    }

}
