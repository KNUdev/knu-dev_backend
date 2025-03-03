package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;

@Mapper(componentModel = "spring", uses = {MultiLanguageFieldMapper.class})
public interface ProjectMapper extends BaseMapper<Project, FullProjectDto> {

////    @Mappings({
////            @Mapping(target = "projectAccounts", source = "projectAccounts"),
////            @Mapping(target = "releaseInfo", source = "releaseInfo")
////    })
////    FullProjectDto toDto(Project project);
////
////    Set<FullProjectDto> toDtos(Set<Project> projects);
//
//    @Mappings({
//            @Mapping(target = "projectId", source = "project.id"),
//            @Mapping(target = "accountId", source = "accountProfile.id")
//    })
//    ProjectAccountDto toProjectAccountDto(SubprojectAccount subprojectAccount);
//
//    Set<ProjectAccountDto> toProjectAccountDtoSet(Set<SubprojectAccount> subprojectAccounts);
}
