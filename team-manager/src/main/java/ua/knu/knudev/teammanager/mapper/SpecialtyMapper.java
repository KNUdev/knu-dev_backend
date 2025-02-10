package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanagerapi.dto.ShortSpecialtyDto;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyDto;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper extends BaseMapper<Specialty, SpecialtyDto> {

    MultiLanguageField map(MultiLanguageFieldDto dto);
    ShortSpecialtyDto toShortDto(Specialty specialty);

}
