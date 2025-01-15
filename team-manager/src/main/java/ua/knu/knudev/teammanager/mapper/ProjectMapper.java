package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.ProjectAccount;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanagerapi.dto.ProjectAccountDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "en", source = "en")
    @Mapping(target = "uk", source = "uk")
    MultiLanguageField map(MultiLanguageFieldDto multiLanguageFieldDto);

    @Mapping(target = "en", source = "en")
    @Mapping(target = "uk", source = "uk")
    MultiLanguageFieldDto map(MultiLanguageField multiLanguageField);

    @Mappings({
            @Mapping(target = "projectAccounts", source = "projectAccounts"),
            @Mapping(target = "releaseInfo", source = "releaseInfo")
    })
    ProjectDto toDto(Project project);

    Set<ProjectDto> toDtos(Set<Project> projects);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "accountId", source = "accountProfile.id")
    ProjectAccountDto toProjectAccountDto(ProjectAccount projectAccount);

    Set<ProjectAccountDto> toProjectAccountDtoSet(Set<ProjectAccount> projectAccounts);
}
