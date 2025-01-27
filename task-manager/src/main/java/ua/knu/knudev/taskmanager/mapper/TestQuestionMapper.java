package ua.knu.knudev.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.taskmanager.domain.TestQuestion;
import ua.knu.knudev.taskmanagerapi.dto.TestQuestionDto;

@Mapper(componentModel = "spring", uses = {QuestionAnswerVariantMapper.class})
public interface TestQuestionMapper extends BaseMapper<TestQuestion, TestQuestionDto> {

    @Override
    @Mapping(source = "answerVariants", target = "answerVariantDtos")
    TestQuestionDto toDto(TestQuestion entity);

    @Override
    @Mapping(source = "answerVariantDtos", target = "answerVariants")
    TestQuestion toDomain(TestQuestionDto dto);
}
