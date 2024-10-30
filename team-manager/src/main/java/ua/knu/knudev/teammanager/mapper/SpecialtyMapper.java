package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Specialty;
import ua.knu.knudev.teammanagerapi.dto.SpecialtyCreationDto;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper extends BaseMapper<Specialty, SpecialtyCreationDto> {
}
