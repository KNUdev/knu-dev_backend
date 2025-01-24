package ua.knu.knudev.taskmanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.taskmanager.domain.Test;
import ua.knu.knudev.taskmanagerapi.dto.FullTestDto;

@Mapper(componentModel = "spring", uses = {TestQuestionMapper.class})
public interface TestMapper extends BaseMapper<Test, FullTestDto> {

}
