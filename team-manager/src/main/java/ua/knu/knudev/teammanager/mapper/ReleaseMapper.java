package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Release;
import ua.knu.knudev.teammanagerapi.dto.ReleaseDto;

@Mapper(componentModel = "spring", uses = {MultiLanguageFieldMapper.class, AccountProfileMapper.class, SubprojectMapper.class})
public interface ReleaseMapper extends BaseMapper<Release, ReleaseDto> {
}
