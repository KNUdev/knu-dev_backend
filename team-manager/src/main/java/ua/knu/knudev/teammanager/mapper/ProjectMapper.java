package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Project;
import ua.knu.knudev.teammanagerapi.dto.FullProjectDto;

@Mapper(componentModel = "spring", uses = {MultiLanguageFieldMapper.class, SubprojectMapper.class})
public interface ProjectMapper extends BaseMapper<Project, FullProjectDto> {
}
