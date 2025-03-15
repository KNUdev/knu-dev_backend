package ua.knu.knudev.teammanagerapi.devprofile;

import org.springframework.context.annotation.Profile;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanagerapi.dto.*;

import java.util.List;

@Profile("dev")
public interface DevProfileTeamManagerApi {
    List<DepartmentWithSpecialtiesDto> createTestDepartments();

    List<ShortAccountProfileDto> createTestAccounts(Integer amount);

    List<ClosedRecruitmentDto> createClosedRecruitments();

    List<ActiveRecruitmentDto> createActiveRecruitments(Integer amount);

    void joinActiveRecruitment();

    List<FullActiveRecruitmentDto> getFullActiveRecruitments();

    List<FullClosedRecruitmentDto> getFullClosedRecruitments(String title, Expertise expertise);
}
