package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;
import ua.knu.knudev.teammanagerapi.dto.SubprojectAccountDto;

@Mapper(componentModel = "spring", uses = {AccountProfileMapper.class, ProjectMapper.class})
public interface SubprojectAccountMapper extends BaseMapper<SubprojectAccount, SubprojectAccountDto> {

    @Mapping(target = "subprojectAccountIdDto.subprojectId", source = "id.subprojectId")
    @Mapping(target = "subprojectAccountIdDto.accountId", source = "id.accountId")
    @Mapping(target = "accountProfileDto", source = "accountProfile")
    SubprojectAccountDto toDto(SubprojectAccount subprojectAccount);

    @Mapping(target = "id.subprojectId", source = "subprojectAccountIdDto.subprojectId")
    @Mapping(target = "id.accountId", source = "subprojectAccountIdDto.accountId")
    @Mapping(target = "accountProfile", source = "accountProfileDto")
    SubprojectAccount toDomain(SubprojectAccountDto subprojectAccountDto);
}
