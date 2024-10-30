package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanagerapi.dto.DepartmentDto;

@Mapper(componentModel = "spring")
public interface DepartmentMapper extends BaseMapper<Department, DepartmentDto> {
}
