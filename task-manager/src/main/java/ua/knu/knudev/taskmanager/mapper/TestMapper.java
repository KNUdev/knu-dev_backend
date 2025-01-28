package ua.knu.knudev.taskmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.taskmanager.domain.TestDomain;
import ua.knu.knudev.taskmanagerapi.dto.FullTestDto;

@Mapper(componentModel = "spring", uses = {TestQuestionMapper.class})
public interface TestMapper extends BaseMapper<TestDomain, FullTestDto> {

    @Mapping(target = "testQuestions", source = "testQuestionDtos")
    TestDomain toDomain(FullTestDto fullTestDto);

    @Mapping(target = "testQuestionDtos", source = "testQuestions")
    FullTestDto toDto(TestDomain testDomain);

}
