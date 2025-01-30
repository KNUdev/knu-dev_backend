package ua.knu.knudev.assessmentmanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.assessmentmanager.domain.QuestionAnswerVariant;
import ua.knu.knudev.assessmentmanagerapi.dto.QuestionAnswerVariantDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface QuestionAnswerVariantMapper extends BaseMapper<QuestionAnswerVariant, QuestionAnswerVariantDto> {
}
