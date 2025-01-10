package ua.knu.knudev.teammanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.MultiLanguageFieldMapper;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

    private final MultiLanguageFieldMapper multiLanguageFieldMapper;
    private final ProjectRepository projectRepository;

    @Override
    public void createProject(ProjectCreationRequest projectCreationRequest) {
        MultiLanguageField name = multiLanguageFieldMapper.toDomain(projectCreationRequest.name());
        MultiLanguageField description = multiLanguageFieldMapper.toDomain(projectCreationRequest.description());

        Project project = Project.builder()
                .name(name)
                .description(description)
                .avatarFilename(projectCreationRequest.avatarFile())
                .tags(projectCreationRequest.tags())
                .startedAt(LocalDate.now())
                .status(projectCreationRequest.status())
                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
                .build();

        projectRepository.save(project);
    }
}
