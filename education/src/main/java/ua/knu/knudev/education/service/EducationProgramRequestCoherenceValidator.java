package ua.knu.knudev.education.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ua.knu.knudev.educationapi.exception.EducationProgramException;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;
import ua.knu.knudev.educationapi.request.ModuleCreationRequest;
import ua.knu.knudev.educationapi.request.SectionCreationRequest;
import ua.knu.knudev.educationapi.request.TopicCreationRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

@Component
public class EducationProgramRequestCoherenceValidator {

    public void validateProgramCreationRequest(EducationProgramCreationRequest program) {
        validateSequence(
                program.sections(),
                SectionCreationRequest::orderIndex,
                "sections in Program"
        );
        program.sections().forEach(this::validateSection);
    }

    private void validateSection(SectionCreationRequest section) {
        validateSequence(
                section.modules(),
                ModuleCreationRequest::orderIndex,
                "modules in Section[orderIndex=" + section.orderIndex() + "]"
        );
        section.modules().forEach(this::validateModule);
    }

    private void validateModule(ModuleCreationRequest module) {
        validateSequence(
                module.topics(),
                TopicCreationRequest::orderIndex,
                "topics in Module[orderIndex=" + module.orderIndex() + "]"
        );
    }

    private <T> void validateSequence(
            Set<T> items,
            ToIntFunction<T> getIndexFn,
            String context
    ) {
        if (items == null || items.isEmpty()) {
            //todo maybe not just return
            return; // or decide if empty is allowed
        }

        int itemsSize = items.size();

        List<Integer> indexes = items.stream()
                .mapToInt(getIndexFn)
                .boxed()
                .toList();

        Set<Integer> uniqueIndexes = new HashSet<>(indexes);
        if (uniqueIndexes.size() != itemsSize) {
            throwEducationProgramException("Duplicate orderIndex found in " + context + " => " + indexes);
        }

        int min = Collections.min(uniqueIndexes);
        int max = Collections.max(uniqueIndexes);

        if (min != 1 || max != itemsSize) {
            throwEducationProgramException(
                    String.format(
                            "Invalid order indexes in %s. Must be strictly 1..%d but found %s (min=%d,max=%d).",
                            context, itemsSize, indexes, min, max
                    )
            );
        }
    }

    private void throwEducationProgramException(String errorMsg) {
        throw new EducationProgramException(errorMsg, HttpStatus.BAD_REQUEST);
    }

}
