package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

@Mapper(componentModel = "spring")
public interface AccountProfileMapper extends BaseMapper<AccountProfile, AccountProfileDto> {
}
