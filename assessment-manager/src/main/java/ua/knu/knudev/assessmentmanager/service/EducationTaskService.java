package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    public Map<LearningUnit, Map<Integer, EducationTaskDto>> uploadAll(
            Map<LearningUnit, Map<Integer, MultipartFile>> educationProgramTasks
    ) {
        List<TaskTempHolder> allTasksToUpload = educationProgramTasks.entrySet().stream()
                .flatMap(learningUnitEntry -> {
                    LearningUnit learningUnit = learningUnitEntry.getKey();
                    Map<Integer, MultipartFile> indexToFileMap = learningUnitEntry.getValue();

                    return indexToFileMap.entrySet().stream()
                            .map(indexFileEntry -> new TaskTempHolder(
                                    learningUnit,
                                    indexFileEntry.getKey(),
                                    indexFileEntry.getValue()
                            ));
                })
                .toList();

        List<EducationTask> tasksForDb = new ArrayList<>();
        Map<LearningUnit, Map<Integer, EducationTaskDto>> result = new HashMap<>();

        allTasksToUpload.forEach(holder -> {
            PdfSubfolder subfolder = PdfSubfolder.getFromLearningUnit(holder.learningUnit);

            String savedFilename = pdfServiceApi.uploadFile(
                    holder.multipartFile,
                    subfolder
            );

            EducationTask newTask = EducationTask.builder()
                    .learningUnit(holder.learningUnit)
                    .taskFilename(savedFilename)
                    .build();
            tasksForDb.add(newTask);

            EducationTaskDto taskDto = EducationTaskDto.builder()
                    .filename(savedFilename)
                    .build();
            result
                    .computeIfAbsent(holder.learningUnit, k -> new HashMap<>())
                    .put(holder.orderIndex, taskDto);
        });

        List<EducationTask> educationTasks = educationTaskRepository.saveAllAndFlush(tasksForDb);
        return buildResult(result, educationTasks);
    }

    @Override
    public String getTask() {
        //todo
        return "";
    }

    private Map<LearningUnit, Map<Integer, EducationTaskDto>> buildResult(
            Map<LearningUnit, Map<Integer, EducationTaskDto>> unmappedResult,
            List<EducationTask> savedEducationTasks
    ) {
        Map<String, EducationTask> filenameTaskMap = savedEducationTasks.stream()
                .collect(Collectors.toMap(EducationTask::getTaskFilename, Function.identity()));

        return unmappedResult.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
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
