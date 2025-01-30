package ua.knu.knudev.assessmentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.assessmentmanager.domain.TestDomain;
import ua.knu.knudev.assessmentmanagerapi.dto.FullTestDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring", uses = {TestQuestionMapper.class})
public interface TestMapper extends BaseMapper<TestDomain, FullTestDto> {

    @Mapping(target = "testQuestions", source = "testQuestionDtos")
    TestDomain toDomain(FullTestDto fullTestDto);

    @Mapping(target = "testQuestionDtos", source = "testQuestions")
    FullTestDto toDto(TestDomain testDomain);

}
