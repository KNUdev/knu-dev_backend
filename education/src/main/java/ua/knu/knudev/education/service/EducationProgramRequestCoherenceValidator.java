package ua.knu.knudev.education.service;

import org.apache.commons.collections4.CollectionUtils;
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

    public void validateProgramOrderSequence(EducationProgramCreationRequest program) {
        List<SectionCreationRequest> sections = program.getSections();

        validateSequence(
                sections,
                SectionCreationRequest::getOrderIndex,
                "sections in Program"
        );
        if(CollectionUtils.isNotEmpty(sections)) {
            sections.forEach(this::validateSection);
        }
    }

    private void validateSection(SectionCreationRequest section) {
        List<ModuleCreationRequest> modules = section.getModules();

        validateSequence(
                modules,
                ModuleCreationRequest::getOrderIndex,
                "modules in Section[orderIndex=" + section.getOrderIndex() + "]"
        );

        if(CollectionUtils.isNotEmpty(modules)) {
            modules.forEach(this::validateModule);
        }
    }

    private void validateModule(ModuleCreationRequest module) {
        validateSequence(
                module.getTopics(),
                TopicCreationRequest::getOrderIndex,
                "topics in Module[orderIndex=" + module.getOrderIndex() + "]"
        );
    }

    private <T> void validateSequence(
            List<T> learningUnitItems,
            ToIntFunction<T> getIndexFn,
            String context
    ) {
        if (learningUnitItems == null || learningUnitItems.isEmpty()) {
            //todo maybe not just return
            return;
        }

        int itemsSize = learningUnitItems.size();

        List<Integer> indexes = learningUnitItems.stream()
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
