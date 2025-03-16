package ua.knu.knudev.teammanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.knu.knudev.knudevcommon.mapper.BaseMapper;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;
import ua.knu.knudev.teammanagerapi.dto.FullActiveRecruitmentDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActiveRecruitmentMapper extends BaseMapper<ActiveRecruitment, FullActiveRecruitmentDto> {

    @Mapping(target = "joinedPeopleAmount",
            expression = "java(activeRecruitment.getCurrentRecruited() != null ? activeRecruitment.getCurrentRecruited().size() : 0)")
    @Mapping(target = "maxCandidates",
            expression = "java(activeRecruitment.getRecruitmentAutoCloseConditions() != null ? " +
                    "activeRecruitment.getRecruitmentAutoCloseConditions().getMaxCandidates() : null)")
    @Mapping(target = "deadlineDate",
            expression = "java(activeRecruitment.getRecruitmentAutoCloseConditions() != null ? " +
                    "activeRecruitment.getRecruitmentAutoCloseConditions().getDeadlineDate() : null)")
    FullActiveRecruitmentDto toDto(ActiveRecruitment activeRecruitment);

    List<FullActiveRecruitmentDto> toDtos(List<ActiveRecruitment> activeRecruitments);
}
