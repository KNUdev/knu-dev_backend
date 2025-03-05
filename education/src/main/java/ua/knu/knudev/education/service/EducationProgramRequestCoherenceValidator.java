package ua.knu.knudev.education.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ua.knu.knudev.educationapi.exception.ProgramException;
import ua.knu.knudev.educationapi.request.ProgramSaveRequest;
import ua.knu.knudev.educationapi.request.ModuleSaveRequest;
import ua.knu.knudev.educationapi.request.SectionSaveRequest;
import ua.knu.knudev.educationapi.request.TopicSaveRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

@Component
public class EducationProgramRequestCoherenceValidator {

    public void validateProgramOrderSequence(ProgramSaveRequest program) {
        if(ObjectUtils.isEmpty(program.getExistingProgramId())) {
            List<SectionSaveRequest> sections = program.getSections();

            validateSequence(
                    sections,
                    SectionSaveRequest::getOrderIndex,
                    "sections in Program"
            );
            if(CollectionUtils.isNotEmpty(sections)) {
                sections.forEach(this::validateSection);
            }
        }
    }

    private void validateSection(SectionSaveRequest section) {
        if(ObjectUtils.isEmpty(section.getExistingSectionId())) {
            List<ModuleSaveRequest> modules = section.getModules();

            validateSequence(
                    modules,
                    ModuleSaveRequest::getOrderIndex,
                    "modules in Section[orderIndex=" + section.getOrderIndex() + "]"
            );

            if (CollectionUtils.isNotEmpty(modules)) {
                modules.forEach(this::validateModule);
            }
        }
    }

    private void validateModule(ModuleSaveRequest module) {
        if(ObjectUtils.isEmpty(module.getExistingModuleId())) {
            validateSequence(
                    module.getTopics(),
                    TopicSaveRequest::getOrderIndex,
                    "topics in Module[orderIndex=" + module.getOrderIndex() + "]"
            );
        }
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
        throw new ProgramException(errorMsg, HttpStatus.BAD_REQUEST);
    }

}
