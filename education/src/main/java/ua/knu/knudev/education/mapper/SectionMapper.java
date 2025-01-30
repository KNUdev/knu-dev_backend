package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.education.domain.program.ProgramSection;
import ua.knu.knudev.educationapi.dto.ProgramSectionDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface SectionMapper extends BaseMapper<ProgramSection, ProgramSectionDto> {
}
