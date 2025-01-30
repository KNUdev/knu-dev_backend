package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.education.domain.EducationProgram;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface ProgramMapper extends BaseMapper<EducationProgram, EducationProgramDto> {

//    @Mapping(target = "finalTaskUrl", source = "finalTask")
    @Mapping(target = "sections", ignore = true)
    EducationProgramDto toDto(EducationProgram domain);
}
