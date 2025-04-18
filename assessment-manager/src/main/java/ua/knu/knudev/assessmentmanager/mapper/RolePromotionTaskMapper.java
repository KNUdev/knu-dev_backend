package ua.knu.knudev.assessmentmanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.assessmentmanager.domain.RolePromotionTask;
import ua.knu.knudev.assessmentmanagerapi.dto.RolePromotionTaskDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface RolePromotionTaskMapper extends BaseMapper<RolePromotionTask, RolePromotionTaskDto> {
}
