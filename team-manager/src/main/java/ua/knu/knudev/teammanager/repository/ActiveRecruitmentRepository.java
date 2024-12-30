package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.ActiveRecruitment;

import java.util.UUID;

public interface ActiveRecruitmentRepository extends JpaRepository<ActiveRecruitment, UUID> {
    boolean existsByExpertiseAndUnit(Expertise expertise, KNUdevUnit unit);

    @Query("""
                SELECT CASE WHEN COUNT(ap) > 0 THEN true ELSE false END
                FROM ActiveRecruitment ar
                JOIN ar.currentRecruited ap
                WHERE ar.id = :activeRecruitmentId
                  AND ap.id = :accountId
            """)
    boolean hasUserJoined(@Param("activeRecruitmentId") UUID activeRecruitmentId,
                          @Param("accountId") UUID accountId);

    @Query("""
                SELECT COUNT(ap)
                FROM ActiveRecruitment ar
                JOIN ar.currentRecruited ap
                WHERE ar.id = :activeRecruitmentId
            """)
    int countRecruited(@Param("activeRecruitmentId") UUID activeRecruitmentId);
}
