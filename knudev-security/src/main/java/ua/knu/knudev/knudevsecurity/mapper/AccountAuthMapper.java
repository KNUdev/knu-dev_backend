package ua.knu.knudev.knudevsecurity.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.knudevsecurity.domain.AccountAuth;
import ua.knu.knudev.knudevsecurity.dto.AccountAuthDto;

@Mapper(componentModel = "spring")
public interface AccountAuthMapper extends BaseMapper<AccountAuth, AccountAuthDto> {

}
