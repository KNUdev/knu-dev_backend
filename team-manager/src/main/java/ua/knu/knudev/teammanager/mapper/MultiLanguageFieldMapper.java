package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

@Mapper(componentModel = "spring")
public interface MultiLanguageFieldMapper extends BaseMapper<MultiLanguageField, MultiLanguageFieldDto> {
}
