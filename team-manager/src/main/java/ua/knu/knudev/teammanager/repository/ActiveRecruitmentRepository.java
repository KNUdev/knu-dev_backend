package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;

import java.util.UUID;

public interface ActiveRecruitmentRepository extends JpaRepository<ActiveRecruitment, UUID> {
    boolean existsByExpertiseAndUnit(Expertise expertise, KNUdevUnit unit);
}
