package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.ProjectAccount;
import ua.knu.knudev.teammanager.domain.ProjectReleaseInfo;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectAccountDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectReleaseInfoDto;
import ua.knu.knudev.teammanagerapi.dto.ShortProjectDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends BaseMapper<Project, FullProjectDto> {

    @Mapping(target = "en", source = "en")
    @Mapping(target = "uk", source = "uk")
    MultiLanguageField map(MultiLanguageFieldDto multiLanguageFieldDto);

    @Mapping(target = "en", source = "en")
    @Mapping(target = "uk", source = "uk")
    MultiLanguageFieldDto map(MultiLanguageField multiLanguageField);

    ProjectReleaseInfoDto map(ProjectReleaseInfo projectReleaseInfo);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "accountId", source = "accountProfile.id")
    ProjectAccountDto map(ProjectAccount projectAccount);

    default Set<ProjectAccountDto> mapAccounts(Set<ProjectAccount> projectAccounts) {
        if (projectAccounts == null) {
            return Set.of();
        }
        return projectAccounts.stream()
                .map(this::map)
                .collect(Collectors.toSet());
    }

    @Override
    default FullProjectDto toDto(Project project) {
        if (project == null) {
            return null;
        }

        ShortProjectDto shortProjectDto = ShortProjectDto.builder()
                .name(map(project.getName()))
                .description(map(project.getDescription()))
                .status(project.getStatus())
                .tags(project.getTags())
                .avatarFilename(project.getAvatarFilename())
                .build();

        FullProjectDto fullProjectDto = FullProjectDto.builder()
                .id(project.getId())
                .startedAt(project.getStartedAt())
                .githubRepoLinks(project.getGithubRepoLinks())
                .releaseInfo(map(project.getReleaseInfo()))
                .projectAccounts(mapAccounts(project.getProjectAccounts()))
                .build();

        fullProjectDto.setName(shortProjectDto.getName());
        fullProjectDto.setDescription(shortProjectDto.getDescription());
        fullProjectDto.setStatus(shortProjectDto.getStatus());
        fullProjectDto.setTags(shortProjectDto.getTags());
        fullProjectDto.setAvatarFilename(shortProjectDto.getAvatarFilename());

        return fullProjectDto;
    }

}
