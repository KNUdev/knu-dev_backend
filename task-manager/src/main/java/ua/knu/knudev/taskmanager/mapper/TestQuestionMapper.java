package ua.knu.knudev.taskmanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.taskmanager.domain.TestQuestion;
import ua.knu.knudev.taskmanagerapi.dto.TestQuestionDto;

@Mapper(componentModel = "spring", uses = {QuestionAnswerVariantMapper.class})
public interface TestQuestionMapper extends BaseMapper<TestQuestion, TestQuestionDto> {
}
