package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanagerapi.dto.FullClosedRecruitmentDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClosedRecruitmentMapper extends BaseMapper<ClosedRecruitment, FullClosedRecruitmentDto> {

    @Mapping(target = "joinedPeopleAmount",
            expression = "java(closedRecruitment.getRecruitmentAnalytics() != null && " +
                    "closedRecruitment.getRecruitmentAnalytics().getJoinedUsers() != null ? " +
                    "closedRecruitment.getRecruitmentAnalytics().getJoinedUsers().size() : 0)")
    @Mapping(target = "maxCandidates",
            expression = "java(closedRecruitment.getRecruitmentAutoCloseConditions() != null ? " +
                    "closedRecruitment.getRecruitmentAutoCloseConditions().getMaxCandidates() : null)")
    FullClosedRecruitmentDto toDto(ClosedRecruitment closedRecruitment);

    List<FullClosedRecruitmentDto> toDtos(List<ClosedRecruitment> closedRecruitments);
}
