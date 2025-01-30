package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.education.domain.MultiLanguageField;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
//todo move to common
public interface EducationMultiLanguageFieldMapper extends BaseMapper<MultiLanguageField, MultiLanguageFieldDto> {
}
