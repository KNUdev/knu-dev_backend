package ua.knu.knudev.teammanager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanager.mapper.ProjectMapper;
import ua.knu.knudev.teammanager.repository.ProjectRepository;
import ua.knu.knudev.teammanagerapi.api.ProjectApi;
import ua.knu.knudev.teammanagerapi.request.ProjectCreationRequest;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectApi {

    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final ImageServiceApi imageServiceApi;

    @Override
    @Transactional
    public void createProject(ProjectCreationRequest projectCreationRequest) {
        MultiLanguageField name = projectMapper.map(projectCreationRequest.name());
        MultiLanguageField description = projectMapper.map(projectCreationRequest.description());

        String filename = imageServiceApi.uploadFile(
                projectCreationRequest.avatarFile(),
                name.getEn(),
                ImageSubfolder.PROJECTS_AVATARS
        );

        Project project = Project.builder()
                .name(name)
                .description(description)
                .avatarFilename(filename)
                .tags(projectCreationRequest.tags())
                .startedAt(LocalDate.now())
                .githubRepoLinks(projectCreationRequest.githubRepoUrls())
                .build();

        projectRepository.save(project);
    }
}
