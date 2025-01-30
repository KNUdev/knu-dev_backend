package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.education.domain.program.ProgramModule;
import ua.knu.knudev.educationapi.dto.ProgramModuleDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper extends BaseMapper<ProgramModule, ProgramModuleDto> {
}
