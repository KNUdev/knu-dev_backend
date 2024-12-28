package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.RecruitmentAutoCloseConditions;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentAutoCloseConditionsDto;

@Mapper(componentModel = "spring")
public interface RecruitmentAutoCloseConditionsMapper extends BaseMapper<RecruitmentAutoCloseConditions, RecruitmentAutoCloseConditionsDto> {
}
