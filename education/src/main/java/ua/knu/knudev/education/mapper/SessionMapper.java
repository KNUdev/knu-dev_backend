package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.educationapi.dto.session.SessionFullDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring", uses = {ProgramMapper.class, SprintMapper.class})
public interface SessionMapper extends BaseMapper<EducationSession, SessionFullDto> {

    @Mappings({
            @Mapping(target = "program", source = "educationProgram"),
            @Mapping(target = "sessionStartDate", source = "startDate"),
            @Mapping(target = "sessionEndDate", source = "estimatedEndDate")
    })
    SessionFullDto toDto(EducationSession domain);

}
