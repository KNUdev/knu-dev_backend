package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;

@Mapper(componentModel = "spring")
public interface MultiLanguageFieldMapper {

    @Mappings({
            @Mapping(target = "en", source = "en"),
            @Mapping(target = "uk", source = "uk")
    })
    MultiLanguageField toDomain(MultiLanguageFieldDto multiLanguageFieldDto);

    @Mappings({
            @Mapping(target = "en", source = "en"),
            @Mapping(target = "uk", source = "uk")
    })
    MultiLanguageFieldDto toDto(MultiLanguageField multiLanguageField);

}
