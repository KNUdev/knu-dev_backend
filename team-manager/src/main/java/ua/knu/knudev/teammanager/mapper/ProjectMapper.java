package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanagerapi.dto.ProjectDto;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends BaseMapper<Project, ProjectDto> {

    MultiLanguageField map(MultiLanguageFieldDto multiLanguageFieldDto);

}
