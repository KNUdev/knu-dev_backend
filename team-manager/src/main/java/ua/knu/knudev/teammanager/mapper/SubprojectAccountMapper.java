package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;
import ua.knu.knudev.teammanagerapi.dto.SubprojectAccountDto;

@Mapper(componentModel = "spring")
public interface SubprojectAccountMapper extends BaseMapper<SubprojectAccount, SubprojectAccountDto> {
}
