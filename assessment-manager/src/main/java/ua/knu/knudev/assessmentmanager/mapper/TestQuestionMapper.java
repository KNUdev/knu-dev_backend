package ua.knu.knudev.assessmentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.assessmentmanager.domain.TestQuestion;
import ua.knu.knudev.assessmentmanagerapi.dto.TestQuestionDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring", uses = {QuestionAnswerVariantMapper.class})
public interface TestQuestionMapper extends BaseMapper<TestQuestion, TestQuestionDto> {

    @Override
    @Mapping(source = "answerVariants", target = "answerVariantDtos")
    TestQuestionDto toDto(TestQuestion entity);

    @Override
    @Mapping(source = "answerVariantDtos", target = "answerVariants")
    TestQuestion toDomain(TestQuestionDto dto);
}
