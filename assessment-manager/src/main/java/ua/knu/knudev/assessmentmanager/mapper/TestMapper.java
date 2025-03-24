package ua.knu.knudev.assessmentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.assessmentmanager.domain.TestDomain;
import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring", uses = {TestQuestionMapper.class})
public interface TestMapper extends BaseMapper<TestDomain, FullTestDto> {

    @Mapping(target = "testQuestions", source = "testQuestionDtos")
    @Mapping(target = "durationConfig.timeUnitPerTextCharacter", source = "timeUnitPerTextCharacter")
    @Mapping(target = "durationConfig.extraTimePerCorrectAnswer", source = "extraTimePerCorrectAnswer")
    @Mapping(target = "testDurationInMinutes", source = "durationInMinutes")
    TestDomain toDomain(FullTestDto fullTestDto);

    @Mapping(target = "testQuestionDtos", source = "testQuestions")
    @Mapping(target = "durationInMinutes", source = "testDurationInMinutes")
    @Mapping(target = "timeUnitPerTextCharacter",
            expression = "java(testDomain.getDurationConfig().getTimeUnitPerTextCharacter())")
    @Mapping(target = "extraTimePerCorrectAnswer",
            expression = "java(testDomain.getDurationConfig().getExtraTimePerCorrectAnswer())")
    FullTestDto toDto(TestDomain testDomain);

}
