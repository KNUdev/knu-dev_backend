package ua.knu.knudev.education.mapper;

import org.mapstruct.Mapper;
import ua.knu.knudev.education.domain.session.Sprint;
import ua.knu.knudev.educationapi.dto.session.SprintSummaryDto;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;

@Mapper(componentModel = "spring")
public interface SprintMapper extends BaseMapper<Sprint, SprintSummaryDto> {
}
