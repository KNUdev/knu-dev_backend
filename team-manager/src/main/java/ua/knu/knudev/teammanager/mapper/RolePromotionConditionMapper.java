package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.RolePromotionConditions;
import ua.knu.knudev.teammanagerapi.dto.RolePromotionConditionDto;

@Mapper(componentModel = "spring")
public interface RolePromotionConditionMapper extends BaseMapper<RolePromotionConditions, RolePromotionConditionDto> {
}
