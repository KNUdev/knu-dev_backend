package ua.knu.knudev.teammanagerapi.devprofile;

import org.springframework.context.annotation.Profile;
import ua.knu.knudev.teammanagerapi.dto.ActiveRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.ClosedRecruitmentDto;
import ua.knu.knudev.teammanagerapi.dto.DepartmentWithSpecialtiesDto;
import ua.knu.knudev.teammanagerapi.dto.ShortAccountProfileDto;

import java.util.List;

@Profile("dev")
public interface DevProfileTeamManagerApi {
    List<DepartmentWithSpecialtiesDto> createTestDepartments();

    List<ShortAccountProfileDto> createTestAccounts(Integer amount);

    List<ClosedRecruitmentDto> createClosedRecruitments();

    List<ActiveRecruitmentDto> createActiveRecruitments();

    void joinActiveRecruitment();

}
