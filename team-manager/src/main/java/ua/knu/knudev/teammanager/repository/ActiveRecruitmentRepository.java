package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;

import java.util.List;

public interface ActiveRecruitmentRepository extends JpaRepository<ActiveRecruitment, String> {

    List<ActiveRecruitment> findActiveRecruitmentByExpertise(Expertise expertise);

}
