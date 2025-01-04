package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanagerapi.dto.DepartmentWithSpecialtiesDto;

@Mapper(componentModel = "spring")
public interface DepartmentWithSpecialtiesMapper extends BaseMapper<Department, DepartmentWithSpecialtiesDto> {
}
