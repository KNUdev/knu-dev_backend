package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.dto.MultiLanguageFieldDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.embeddable.MultiLanguageField;
import ua.knu.knudev.teammanagerapi.dto.ShortDepartmentDto;

@Mapper(componentModel = "spring")
public interface ShortDepartmentMapper extends BaseMapper<Department, ShortDepartmentDto> {

    MultiLanguageField map(MultiLanguageFieldDto dto);


}
