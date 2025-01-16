package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;

@Mapper(componentModel = "spring")
public interface RecruitmentMapper extends BaseMapper<ActiveRecruitment, ActiveRecruitmentDto> {

}
