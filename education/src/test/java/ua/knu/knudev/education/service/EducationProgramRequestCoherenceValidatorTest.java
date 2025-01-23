package ua.knu.knudev.education.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
//todo refactor
class EducationProgramRequestCoherenceValidatorTest {

    @InjectMocks
    private EducationProgramRequestCoherenceValidator validator;

    /**
     * Utility to build a quick MultiLanguageFieldDto for test
     */
    private MultiLanguageFieldDto dummyText(String text) {
        // Adjust if your MultiLanguageFieldDto expects different constructor args
        return new MultiLanguageFieldDto(text, text);
    }

    // ---------------------------------------------------------
    // VALID SCENARIOS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Valid: 2 sections with correct consecutive indexes + each section has modules in 1..N and modules have topics in 1..N => passes")
    void validateProgram_validRequest_passes() {
        // Section 1 => orderIndex=1
        SectionCreationRequest section1 = new SectionCreationRequest(
                dummyText("S1"), dummyText("Section 1 desc"),
                Set.of(
                        // Module 1 => orderIndex=1, topics => [1,2]
                        new ModuleCreationRequest(
                                dummyText("M1"), dummyText("Module 1 desc"),
                                Set.of(
                                        new TopicCreationRequest(dummyText("T1"), dummyText("topic1"), null, Set.of(), 1, Set.of()),
                                        new TopicCreationRequest(dummyText("T2"), dummyText("topic2"), null, Set.of(), 2, Set.of())
                                ),
                                null,
                                1,
                                null
                        ),
                        // Module 2 => orderIndex=2, topics => [1]
                        new ModuleCreationRequest(
                                dummyText("M2"), dummyText("Module 2 desc"),
                                Set.of(
                                        new TopicCreationRequest(dummyText("T3"), dummyText("topic3"), null, Set.of(), 1, Set.of())
                                ),
                                null,
                                2,
                                null
                        )
                ),
                null,
                1,
                null
        );

        // Section 2 => orderIndex=2
        SectionCreationRequest section2 = new SectionCreationRequest(
                dummyText("S2"), dummyText("Section 2 desc"),
                Set.of(
                        // Module => orderIndex=1, topics => [1,2]
                        new ModuleCreationRequest(
                                dummyText("M3"), dummyText("Module 3 desc"),
                                Set.of(
                                        new TopicCreationRequest(dummyText("T4"), dummyText("topic4"), null, Set.of(), 1, Set.of()),
                                        new TopicCreationRequest(dummyText("T5"), dummyText("topic5"), null, Set.of(), 2, Set.of())
                                ),
                                null,
                                1,
                                null
                        )
                ),
                null,
                2,
                null
        );

        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"),
                dummyText("Valid program"),
                Set.of(section1, section2),
                Expertise.BACKEND,
                null
        );

        // Should NOT throw
        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(programRequest));
    }

    @Test
    @DisplayName("Valid: Program with no sections => passes if domain allows empty sections")
    void validateProgram_emptySectionsAllowed_passes() {
        // Just an empty set of sections
        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("Empty program"),
                Set.of(), // no sections
                Expertise.BACKEND,
                null
        );

        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(programRequest));
    }

    @Test
    @DisplayName("Valid: One section, one module, one topic, all index=1 => passes")
    void validateProgram_singleElementsAllIndexOne_passes() {
        // Single section => orderIndex=1
        // single module => orderIndex=1
        // single topic => orderIndex=1
        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(
                        new ModuleCreationRequest(
                                dummyText("M1"), dummyText("desc"),
                                Set.of(
                                        new TopicCreationRequest(dummyText("T1"), dummyText("desc"), null, Set.of(), 1, Set.of())
                                ),
                                null,
                                1,
                                null
                        )
                ),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.BACKEND,
                null
        );

        assertDoesNotThrow(() -> validator.validateProgramCreationRequest(request));
    }

    // ---------------------------------------------------------
    // INVALID SCENARIOS: SECTIONS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Invalid: Program has 2 sections with the same orderIndex => throws exception")
    void validateProgram_duplicateSectionOrderIndexes_throws() {
        SectionCreationRequest s1 = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(), null, 1, null
        );
        SectionCreationRequest s2 = new SectionCreationRequest(
                dummyText("S2"), dummyText("desc"),
                Set.of(), null, 1, null // same index as s1
        );

        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(s1, s2),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(programRequest);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Duplicate orderIndex found in sections in Program"));
    }

    @Test
    @DisplayName("Invalid: Program has 2 sections but skipping an index => e.g. orderIndex=1, orderIndex=3 => throws exception")
    void validateProgram_sectionsSkippingIndex_throws() {
        // Section indexes: 1, 3 => skipping 2
        SectionCreationRequest s1 = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"), Set.of(), null, 1, null
        );
        SectionCreationRequest s2 = new SectionCreationRequest(
                dummyText("S2"), dummyText("desc"), Set.of(), null, 3, null
        );

        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(s1, s2),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(programRequest);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in sections in Program"));
    }

    @Test
    @DisplayName("Invalid: Program has a section with zero orderIndex => fails because min=1 is expected")
    void validateProgram_sectionZeroIndex_throws() {
        SectionCreationRequest sectionZero = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(), null, 0, null // zero index
        );

        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(sectionZero),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(programRequest);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in sections in Program"));
        // Because we expect 1..N, zero is outside that range
    }

    @Test
    @DisplayName("Invalid: Program has a section with negative orderIndex => fails as well")
    void validateProgram_sectionNegativeIndex_throws() {
        SectionCreationRequest negativeSection = new SectionCreationRequest(
                dummyText("SNeg"), dummyText("desc"),
                Set.of(), null, -1, null // negative index
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(negativeSection),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in sections in Program"));
    }

    // ---------------------------------------------------------
    // INVALID SCENARIOS: MODULES
    // ---------------------------------------------------------

    @Test
    @DisplayName("Invalid: A section has 2 modules with the same orderIndex => throws exception")
    void validateProgram_sectionModulesDuplicateIndex_throws() {
        ModuleCreationRequest m1 = new ModuleCreationRequest(
                dummyText("M1"), dummyText("desc"),
                Set.of(), null, 2, null
        );
        ModuleCreationRequest m2 = new ModuleCreationRequest(
                dummyText("M2"), dummyText("desc"),
                Set.of(), null, 2, null // same as m1
        );

        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("Sec1"), dummyText("desc"),
                Set.of(m1, m2),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Duplicate orderIndex found in topics in Section"));
    }

    @Test
    @DisplayName("Invalid: A section has modules with skipping indexes => e.g. module indexes are 1,3 => throws exception")
    void validateProgram_sectionModulesSkippingOrder_throws() {
        // Example from your code
        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("S1"), dummyText("Section 1 desc"),
                Set.of(
                        new ModuleCreationRequest(
                                dummyText("M1"), dummyText("M1 desc"),
                                Set.of(), null, 1, null
                        ),
                        new ModuleCreationRequest(
                                dummyText("M2"), dummyText("M2 desc"),
                                Set.of(), null, 3, null // skipping #2
                        )
                ),
                null,
                1,
                null
        );

        EducationProgramCreationRequest programRequest = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("Program desc"),
                Set.of(section),
                Expertise.FRONTEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(programRequest);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in topics in Section"));
    }

    // ---------------------------------------------------------
    // INVALID SCENARIOS: TOPICS
    // ---------------------------------------------------------

    @Test
    @DisplayName("Invalid: A module has 2 topics with the same orderIndex => throws exception")
    void validateProgram_moduleDuplicateTopicIndex_throws() {
        ModuleCreationRequest module = new ModuleCreationRequest(
                dummyText("M1"), dummyText("desc"),
                Set.of(
                        new TopicCreationRequest(dummyText("T1"), dummyText("desc"), null, Set.of(), 1, Set.of()),
                        new TopicCreationRequest(dummyText("T2"), dummyText("desc"), null, Set.of(), 1, Set.of()) // same index
                ),
                null,
                1,
                null
        );

        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("Sec1"), dummyText("desc"),
                Set.of(module),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.FRONTEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Duplicate orderIndex found in topics in Module"));
    }

    @Test
    @DisplayName("Invalid: A module has topics skipping indexes => e.g. topic indexes are 1, 3 => throws exception")
    void validateProgram_moduleSkippingTopicOrder_throws() {
        ModuleCreationRequest module = new ModuleCreationRequest(
                dummyText("M1"), dummyText("desc"),
                Set.of(
                        new TopicCreationRequest(dummyText("T1"), dummyText("desc"), null, Set.of(), 1, Set.of()),
                        new TopicCreationRequest(dummyText("T2"), dummyText("desc"), null, Set.of(), 3, Set.of()) // skipping 2
                ),
                null,
                1,
                null
        );

        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(module),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.FRONTEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in topics in Module"));
    }

    @Test
    @DisplayName("Invalid: A module has topic with zero index => fails because expecting 1..N")
    void validateProgram_moduleZeroTopicIndex_throws() {
        ModuleCreationRequest module = new ModuleCreationRequest(
                dummyText("M1"), dummyText("desc"),
                Set.of(
                        new TopicCreationRequest(dummyText("T1"), dummyText("desc"), null, Set.of(), 0, Set.of()) // zero
                ),
                null,
                1,
                null
        );

        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(module),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in topics in Module"));
    }

    @Test
    @DisplayName("Invalid: A module has topic with negative index => fails too")
    void validateProgram_moduleNegativeTopicIndex_throws() {
        ModuleCreationRequest module = new ModuleCreationRequest(
                dummyText("M1"), dummyText("desc"),
                Set.of(
                        new TopicCreationRequest(dummyText("T1"), dummyText("desc"), null, Set.of(), -5, Set.of())
                ),
                null,
                1,
                null
        );

        SectionCreationRequest section = new SectionCreationRequest(
                dummyText("S1"), dummyText("desc"),
                Set.of(module),
                null,
                1,
                null
        );

        EducationProgramCreationRequest request = new EducationProgramCreationRequest(
                dummyText("Prog"), dummyText("desc"),
                Set.of(section),
                Expertise.BACKEND,
                null
        );

        Executable action = () -> validator.validateProgramCreationRequest(request);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, action);
        assertTrue(ex.getMessage().contains("Invalid order indexes in topics in Module"));
    }
}
