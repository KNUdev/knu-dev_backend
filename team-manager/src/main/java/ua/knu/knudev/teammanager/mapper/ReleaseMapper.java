package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Release;
import ua.knu.knudev.teammanager.github.dto.ReleaseDto;

@Mapper(componentModel = "spring")
public interface ReleaseMapper extends BaseMapper<Release, ReleaseDto> {
}
