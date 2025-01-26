package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.assessmentmanager.domain.EducationTask;
import ua.knu.knudev.assessmentmanager.repository.EducationTaskRepository;
import ua.knu.knudev.assessmentmanagerapi.api.EducationTaskApi;
import ua.knu.knudev.assessmentmanagerapi.dto.EducationTaskDto;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTaskService implements EducationTaskApi {

    private final EducationTaskRepository educationTaskRepository;
    private final PDFServiceApi pdfServiceApi;
    private final TransactionTemplate transactionTemplate;

    //  Map<LearningUnit, Map<Integer, String>> - Integer - orderIndex, String - filename
    //new solution - instead of string use EducationTaskDto
    @Override
    public Map<LearningUnit, Map<Integer, EducationTaskDto>> uploadAll(
            Map<LearningUnit, Map<Integer, MultipartFile>> educationProgramTasks
    ) {
        // 1) Flatten the input into a simple list that keeps track of (learningUnit, orderIndex, file).
        List<TaskTempHolder> allTasksToUpload = educationProgramTasks.entrySet().stream()
                .flatMap(learningUnitEntry -> {
                    LearningUnit lu = learningUnitEntry.getKey();
                    Map<Integer, MultipartFile> indexToFileMap = learningUnitEntry.getValue();

                    return indexToFileMap.entrySet().stream()
                            .map(indexFileEntry -> new TaskTempHolder(
                                    lu,
                                    indexFileEntry.getKey(),
                                    indexFileEntry.getValue()
                            ));
                })
                .toList();

        // 2) We’ll store all EducationTask entities here (for DB) and
        //    also build the final result map in parallel.
        List<EducationTask> tasksForDb = new ArrayList<>();
        Map<LearningUnit, Map<Integer, EducationTaskDto>> result = new HashMap<>();

        // 3) For each (learningUnit, orderIndex, MultipartFile) -> upload + create entity
        for (TaskTempHolder holder : allTasksToUpload) {
            // If you want a custom filename, build it here:
            //todo change
            String customFilename = UUID.randomUUID().toString();

            // Subfolder from the LearningUnit
            PdfSubfolder subfolder = PdfSubfolder.getFromLearningUnit(holder.learningUnit);

            // 3a) Upload the file
            String savedFilename = pdfServiceApi.uploadFile(
                    holder.multipartFile,
                    customFilename,
                    subfolder
            );

            // 3b) Build the entity (no orderIndex in your DB, so we omit it)
            EducationTask newTask = EducationTask.builder()
                    .learningUnit(holder.learningUnit)
                    .taskFilename(savedFilename)
                    .build();

            tasksForDb.add(newTask);

            // 3c) Fill in the final result mapping
            //     (learningUnit -> (orderIndex -> savedFilename))
            EducationTaskDto taskDto = EducationTaskDto.builder()
                    .filename(savedFilename)
                    .build();
            result
                    .computeIfAbsent(holder.learningUnit, k -> new HashMap<>())
                    .put(holder.orderIndex, taskDto);
        }

        //todo maybe do not use transactionTemplate
//        transactionTemplate.executeWithoutResult(status -> {
//            educationTaskRepository.saveAllAndFlush(tasksForDb);
//        });
        List<EducationTask> educationTasks = educationTaskRepository.saveAllAndFlush(tasksForDb);

        return buildResult(result, educationTasks);
    }

    private Map<LearningUnit, Map<Integer, EducationTaskDto>> buildResult(
            Map<LearningUnit, Map<Integer, EducationTaskDto>> unmappedResult, List<EducationTask> savedEducationTasks
    ) {

        Map<String, EducationTask> filenameTaskMap = savedEducationTasks.stream()
                .collect(Collectors.toMap(EducationTask::getTaskFilename, Function.identity()));

        // Preserve the LearningUnit key
        // Preserve the Integer key
        return unmappedResult.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Preserve the LearningUnit key
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey, // Preserve the Integer key
                                        innerEntry -> {
                                            EducationTaskDto dto = innerEntry.getValue();
                                            EducationTask task = filenameTaskMap.get(dto.getFilename());

                                            if (task != null) {
                                                dto.setId(task.getId());
                                            }

                                            return dto;
                                        }
                                ))
                ));
    }

    @Override
    public String upload(MultipartFile file) {
        return "";
    }

    @Override
    public String getById(UUID id) {
        return "";
    }

    private record TaskTempHolder(
            LearningUnit learningUnit,
            int orderIndex,
            MultipartFile multipartFile
    ) {
    }

}
