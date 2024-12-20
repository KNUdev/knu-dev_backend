package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.Recruitment;
import ua.knu.knudev.teammanagerapi.dto.RecruitmentDto;

@Mapper(componentModel = "spring")
public interface RecruitmentMapper extends BaseMapper<Recruitment, RecruitmentDto> {
}
