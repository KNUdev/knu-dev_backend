package ua.knu.knudev.taskmanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.taskmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.taskmanagerapi.dto.QuestionAnswerVariantDto;

@Mapper(componentModel = "spring")
public interface QuestionAnswerVariantMapper extends BaseMapper<QuestionAnswerVariant, QuestionAnswerVariantDto> {
}
