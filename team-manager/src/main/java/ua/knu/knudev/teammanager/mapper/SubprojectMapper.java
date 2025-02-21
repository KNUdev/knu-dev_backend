package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Subproject;
import ua.knu.knudev.teammanagerapi.dto.SubprojectDto;

@Mapper(componentModel = "spring")
public interface SubprojectMapper extends BaseMapper<Subproject, SubprojectDto> {
}
