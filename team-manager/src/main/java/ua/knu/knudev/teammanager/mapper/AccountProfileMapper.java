package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanagerapi.dto.AccountProfileDto;

@Mapper(componentModel = "spring", uses = {SubprojectAccountMapper.class, MultiLanguageFieldMapper.class})
public interface AccountProfileMapper extends BaseMapper<AccountProfile, AccountProfileDto> {

//    todo add mapping for academicUnitsIds
    @Mappings({
            @Mapping(target = "fullName.firstName", source = "firstName"),
            @Mapping(target = "fullName.lastName", source = "lastName"),
            @Mapping(target = "fullName.middleName", source = "middleName"),
            @Mapping(target = "departmentName", source = "department.name"),
            @Mapping(target = "specialtyName", source = "specialty.name"),
            @Mapping(target = "registeredAt", source = "registrationDate")
    })
    AccountProfileDto toDto(AccountProfile accountProfile);

    @Mappings({
            @Mapping(target = "firstName", source = "fullName.firstName"),
            @Mapping(target = "lastName", source = "fullName.lastName"),
            @Mapping(target = "middleName", source = "fullName.middleName"),
            @Mapping(target = "department.name", source = "departmentName"),
            @Mapping(target = "specialty.name", source = "specialtyName"),
            @Mapping(target = "registrationDate", source = "registeredAt")
    })
    AccountProfile toDomain(AccountProfileDto accountProfileDto);
}
