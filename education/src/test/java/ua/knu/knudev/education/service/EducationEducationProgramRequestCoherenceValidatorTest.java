package ua.knu.knudev.education.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.educationapi.exception.ProgramException;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EducationEducationProgramRequestCoherenceValidatorTest {

    @InjectMocks
    private EducationProgramRequestCoherenceValidator validator;

    private void assertValidationThrowsError(ProgramSaveRequest programCreationRequest) {
        assertThrows(
                ProgramException.class,
                () -> validator.validateProgramOrderSequence(programCreationRequest)
        );
    }

    private ProgramSaveRequest createProgram(List<SectionSaveRequest> sections) {
        return new ProgramSaveRequest(
                getDummyText("Prog"),
                getDummyText("desc"),
                null,
                null,
                sections,
                Expertise.BACKEND
        );
    }

    private SectionSaveRequest createSection(int orderIndex, List<ModuleSaveRequest> modules) {
        String nameAndDesc = "Section" + orderIndex;

        return new SectionSaveRequest(
                getDummyText(nameAndDesc),
                getDummyText(nameAndDesc),
                null,
                null,
                modules,
                orderIndex
        );
    }

    private ModuleSaveRequest createModule(int orderIndex, List<TopicSaveRequest> topics) {
        String nameAndDesc = "Module" + orderIndex;

        return new ModuleSaveRequest(
                getDummyText(nameAndDesc),
                getDummyText(nameAndDesc),
                null,
                null,
                topics,
                orderIndex
        );
    }

    private TopicSaveRequest createTopic(String nameAndDesc, int orderIndex) {
        return new TopicSaveRequest(
                getDummyText(nameAndDesc),
                getDummyText(nameAndDesc),
                null,
                null,
                null,
                orderIndex,
                null,
                10
        );
    }

    private TopicSaveRequest createTopic(int orderIndex) {
        String nameAndDesc = "Topic" + orderIndex;

        return new TopicSaveRequest(
                getDummyText(nameAndDesc),
                getDummyText(nameAndDesc),
                null,
                null,
                null,
                orderIndex,
                null,
                10
        );
    }

    private MultiLanguageFieldDto getDummyText(String text) {
        return new MultiLanguageFieldDto(text, text);
    }

    @Test
    @DisplayName("Valid: sections with correct consecutive indexes + each section has modules in 1..N and modules have topics in 1..N => passes")
    void should_ValidateProgramSuccessfully_When_InputHasValidSequence() {
        SectionSaveRequest section1 = createSection(1, List.of(
                createModule(1, List.of(
                        createTopic(1),
                        createTopic(2)
                )),
                createModule(2, List.of(
                        createTopic(1)
                ))
        ));

        SectionSaveRequest section2 = createSection(2, List.of(
                createModule(1, List.of(
                        createTopic(1),
                        createTopic(2)
                ))
        ));

        ProgramSaveRequest programRequest = createProgram(List.of(section1, section2));

        assertDoesNotThrow(() -> validator.validateProgramOrderSequence(programRequest));
    }

    @Test
    @DisplayName("Valid: Program with no sections => passes if domain allows empty sections")
    void should_ValidateSuccessfully_When_SectionDoesNotHaveModules() {
        ProgramSaveRequest programRequest = createProgram(List.of());

        assertDoesNotThrow(() -> validator.validateProgramOrderSequence(programRequest));
    }

    @Test
    @DisplayName("Valid: One section, one module, one topic, all index=1 => passes")
    void should_SuccessfullyValidateProgram_When_AllUnitsHaveOnly1SubunitWithIndexOf1() {
        SectionSaveRequest section = createSection(1, List.of(
                createModule(1, List.of(
                        createTopic(1)
                ))
        ));

        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertDoesNotThrow(() -> validator.validateProgramOrderSequence(programRequest));
    }

    @Test
    @DisplayName("Invalid: Program has 2 sections with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_Given2SectionsWithSameIndex() {
        SectionSaveRequest section1 = createSection(1, List.of()).toBuilder()
                .name(getDummyText("S1"))
                .build();
        SectionSaveRequest section2 = createSection(1, List.of()).toBuilder()
                .name(getDummyText("S2"))
                .build();

        ProgramSaveRequest programRequest = createProgram(List.of(section1, section2));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has 2 sections but skipping an index => e.g. orderIndex=1, orderIndex=3 => throws exception")
    void should_ThrowEducationProgramException_When_GivenSectionIndexesOfSingleAreNotSequential() {
        SectionSaveRequest section1 = createSection(1, List.of());
        SectionSaveRequest section2 = createSection(3, List.of());

        ProgramSaveRequest programRequest = createProgram(List.of(section1, section2));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has a section with zero orderIndex => fails because min=1 is expected")
    void should_ThrowEducationProgramException_When_IndexStartsFrom0() {
        SectionSaveRequest sectionIndexZero = createSection(0, List.of());

        ProgramSaveRequest programRequest = createProgram(List.of(sectionIndexZero));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has a section with negative orderIndex => fails as well")
    void should_ThrowEducationProgramException_When_IndexIsNegative() {
        SectionSaveRequest negativeIndexSection = createSection(-5, List.of());

        ProgramSaveRequest programRequest = createProgram(List.of(negativeIndexSection));

        assertValidationThrowsError(programRequest);
    }


    @Test
    @DisplayName("Invalid: A section has 2 modules with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_SectionHasModulesWithSameIndexes() {
        ModuleSaveRequest module1 = createModule(2, List.of()).toBuilder()
                .name(getDummyText("M1"))
                .build();
        ModuleSaveRequest module2 = createModule(2, List.of()).toBuilder()
                .name(getDummyText("M2"))
                .build();

        SectionSaveRequest section = createSection(1, List.of(module1, module2));
        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A section has modules with skipping indexes => e.g. module indexes are 1,3 => throws exception")
    void should_ThrowEducationProgramException_When_IndexesOfModulesAreNotSequential() {
        SectionSaveRequest section = createSection(1, List.of(
                createModule(1, List.of()),
                createModule(3, List.of())
        ));

        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has 2 topics with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_ModulesHasTopicsWithSame() {
        ModuleSaveRequest module = createModule(1, List.of(
                createTopic(1).toBuilder().name(getDummyText("T1")).build(),
                createTopic(1).toBuilder().name(getDummyText("T2")).build()
        ));

        SectionSaveRequest section = createSection(1, List.of(module));
        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topics skipping indexes => e.g. topic indexes are 1, 3 => throws exception")
    void should_ThrowEducationProgramException_When_IndexesOfTopicsAreNotSequential() {
        ModuleSaveRequest module = createModule(1, List.of(
                createTopic("T1", 1),
                createTopic("T2", 3)
        ));

        SectionSaveRequest section = createSection(1, List.of(module));
        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topic with zero index => fails because expecting 1..N")
    void should_ThrowEducationProgramException_When_IndexOfTopicStartsFrom0() {
        ModuleSaveRequest module = createModule(1, List.of(
                createTopic(0)
        ));

        SectionSaveRequest section = createSection(1, List.of(module));
        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topic with negative index => fails too")
    void should_ThrowEducationProgramException_When_ThereIsNegativeTopicIndex() {
        ModuleSaveRequest module = createModule(1, List.of(
                createTopic(-5)
        ));

        SectionSaveRequest section = createSection(1, List.of(module));
        ProgramSaveRequest programRequest = createProgram(List.of(section));

        assertValidationThrowsError(programRequest);
    }

}
