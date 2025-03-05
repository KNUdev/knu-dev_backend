package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.education.domain.program.ProgramTopic;
import ua.knu.knudev.educationapi.dto.ProgramTopicDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface TopicMapper extends BaseMapper<ProgramTopic, ProgramTopicDto> {

    @Mapping(target = "learningResources", source = "learningResources")
    ProgramTopicDto toDto(ProgramTopic programTopic);
}
