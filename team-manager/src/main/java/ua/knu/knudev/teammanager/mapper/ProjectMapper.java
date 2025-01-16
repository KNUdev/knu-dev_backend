package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.ProjectAccount;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;
import ua.knu.knudev.teammanagerapi.dto.ProjectAccountDto;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {MultiLanguageFieldMapper.class})
public interface ProjectMapper extends BaseMapper<Project, FullProjectDto> {

    @Mappings({
            @Mapping(target = "projectAccounts", source = "projectAccounts"),
            @Mapping(target = "releaseInfo", source = "releaseInfo")
    })
    FullProjectDto toDto(Project project);

    Set<FullProjectDto> toDtos(Set<Project> projects);

    @Mappings({
            @Mapping(target = "projectId", source = "project.id"),
            @Mapping(target = "accountId", source = "accountProfile.id")
    })
    ProjectAccountDto toProjectAccountDto(ProjectAccount projectAccount);

    Set<ProjectAccountDto> toProjectAccountDtoSet(Set<ProjectAccount> projectAccounts);
}
