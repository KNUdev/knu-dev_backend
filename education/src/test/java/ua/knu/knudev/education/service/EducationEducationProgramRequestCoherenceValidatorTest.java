package ua.knu.knudev.education.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.educationapi.exception.EducationProgramException;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EducationEducationProgramRequestCoherenceValidatorTest {

    @InjectMocks
    private EducationProgramRequestCoherenceValidator validator;

    private void assertValidationThrowsError(EducationProgramCreationRequest programCreationRequest) {
        assertThrows(
                EducationProgramException.class,
                () -> validator.validateProgramCreationRequest(programCreationRequest)
        );
    }

    private EducationProgramCreationRequest createProgram(Set<SectionCreationRequest> sections) {
        return new EducationProgramCreationRequest(
                null,
                getDummyText("Prog"), getDummyText("desc"),
                sections,
                Expertise.BACKEND,
                null
        );
    }

    private SectionCreationRequest createSection(int orderIndex, Set<ModuleCreationRequest> modules) {
        String nameAndDesc = "Section" + orderIndex;

        return new SectionCreationRequest(
                null,
                getDummyText(nameAndDesc), getDummyText(nameAndDesc),
                modules,
                null,
                orderIndex,
                null
        );
    }

    private ModuleCreationRequest createModule(int orderIndex, Set<TopicCreationRequest> topics) {
        String nameAndDesc = "Module" + orderIndex;

        return new ModuleCreationRequest(
                null,
                getDummyText(nameAndDesc), getDummyText(nameAndDesc),
                topics,
                null,
                orderIndex,
                null
        );
    }

    private TopicCreationRequest createTopic(String nameAndDesc, int orderIndex) {
        return new TopicCreationRequest(null, getDummyText(nameAndDesc), getDummyText(nameAndDesc),
                null, Set.of(), orderIndex, Set.of());
    }

    private TopicCreationRequest createTopic(int orderIndex) {
        String nameAndDesc = "Topic" + orderIndex;

        return new TopicCreationRequest(null, getDummyText(nameAndDesc), getDummyText(nameAndDesc),
                null, Set.of(), orderIndex, Set.of());
    }

    private MultiLanguageFieldDto getDummyText(String text) {
        return new MultiLanguageFieldDto(text, text);
    }

    @Test
    @DisplayName("Valid: sections with correct consecutive indexes + each section has modules in 1..N and modules have topics in 1..N => passes")
    void should_ValidateProgramSuccessfully_When_InputHasValidSequence() {
        SectionCreationRequest section1 = createSection(1, Set.of(
                createModule(1, Set.of(
                        createTopic(1),
                        createTopic(2)
                )),
                createModule(2, Set.of(
                        createTopic(1)
                ))
        ));

        SectionCreationRequest section2 = createSection(2, Set.of(
                createModule(1, Set.of(
                        createTopic(1),
                        createTopic(2)
                ))
        ));

        EducationProgramCreationRequest programRequest = createProgram(Set.of(section1, section2));

        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(programRequest));
    }

    @Test
    @DisplayName("Valid: Program with no sections => passes if domain allows empty sections")
    void should_ValidateSuccessfully_When_SectionDoesNotHaveModules() {
        EducationProgramCreationRequest programRequest = createProgram(Set.of());

        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(programRequest));
    }

    @Test
    @DisplayName("Valid: One section, one module, one topic, all index=1 => passes")
    void should_SuccessfullyValidateProgram_When_AllUnitsHaveOnly1SubunitWithIndexOf1() {
        SectionCreationRequest section = createSection(1, Set.of(
                createModule(1, Set.of(
                        createTopic(1)
                ))
        ));

        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(programRequest));
    }

    @Test
    @DisplayName("Invalid: Program has 2 sections with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_Given2SectionsWithSameIndex() {
        SectionCreationRequest section1 = createSection(1, Set.of()).toBuilder()
                .name(getDummyText("S1"))
                .build();
        SectionCreationRequest section2 = createSection(1, Set.of()).toBuilder()
                .name(getDummyText("S2"))
                .build();

        EducationProgramCreationRequest programRequest = createProgram(Set.of(section1, section2));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has 2 sections but skipping an index => e.g. orderIndex=1, orderIndex=3 => throws exception")
    void should_ThrowEducationProgramException_When_GivenSectionIndexesOfSingleAreNotSequential() {
        SectionCreationRequest section1 = createSection(1, Set.of());
        SectionCreationRequest section2 = createSection(3, Set.of());

        EducationProgramCreationRequest programRequest = createProgram(Set.of(section1, section2));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has a section with zero orderIndex => fails because min=1 is expected")
    void should_ThrowEducationProgramException_When_IndexStartsFrom0() {
        SectionCreationRequest sectionIndexZero = createSection(0, Set.of());

        EducationProgramCreationRequest programRequest = createProgram(Set.of(sectionIndexZero));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: Program has a section with negative orderIndex => fails as well")
    void should_ThrowEducationProgramException_When_IndexIsNegative() {
        SectionCreationRequest negativeIndexSection = createSection(-5, Set.of());

        EducationProgramCreationRequest programRequest = createProgram(Set.of(negativeIndexSection));

        assertValidationThrowsError(programRequest);
    }


    @Test
    @DisplayName("Invalid: A section has 2 modules with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_SectionHasModulesWithSameIndexes() {
        ModuleCreationRequest module1 = createModule(2, Set.of()).toBuilder()
                .name(getDummyText("M1"))
                .build();
        ModuleCreationRequest module2 = createModule(2, Set.of()).toBuilder()
                .name(getDummyText("M2"))
                .build();

        SectionCreationRequest section = createSection(1, Set.of(module1, module2));
        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A section has modules with skipping indexes => e.g. module indexes are 1,3 => throws exception")
    void should_ThrowEducationProgramException_When_IndexesOfModulesAreNotSequential() {
        SectionCreationRequest section = createSection(1, Set.of(
                createModule(1, Set.of()),
                createModule(3, Set.of())
        ));

        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has 2 topics with the same orderIndex => throws exception")
    void should_ThrowEducationProgramException_When_ModulesHasTopicsWithSame() {
        ModuleCreationRequest module = createModule(1, Set.of(
                createTopic(1).toBuilder().name(getDummyText("T1")).build(),
                createTopic(1).toBuilder().name(getDummyText("T2")).build()
        ));

        SectionCreationRequest section = createSection(1, Set.of(module));
        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topics skipping indexes => e.g. topic indexes are 1, 3 => throws exception")
    void should_ThrowEducationProgramException_When_IndexesOfTopicsAreNotSequential() {
        ModuleCreationRequest module = createModule(1, Set.of(
                createTopic("T1", 1),
                createTopic("T2", 3)
        ));

        SectionCreationRequest section = createSection(1, Set.of(module));
        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topic with zero index => fails because expecting 1..N")
    void should_ThrowEducationProgramException_When_IndexOfTopicStartsFrom0() {
        ModuleCreationRequest module = createModule(1, Set.of(
                createTopic(0)
        ));

        SectionCreationRequest section = createSection(1, Set.of(module));
        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

    @Test
    @DisplayName("Invalid: A module has topic with negative index => fails too")
    void should_ThrowEducationProgramException_When_ThereIsNegativeTopicIndex() {
        ModuleCreationRequest module = createModule(1, Set.of(
                createTopic(-5)
        ));

        SectionCreationRequest section = createSection(1, Set.of(module));
        EducationProgramCreationRequest programRequest = createProgram(Set.of(section));

        assertValidationThrowsError(programRequest);
    }

}
