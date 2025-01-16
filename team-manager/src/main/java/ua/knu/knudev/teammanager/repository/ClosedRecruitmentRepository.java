package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;

import java.util.UUID;

public interface ClosedRecruitmentRepository extends JpaRepository<ClosedRecruitment, UUID> {

    @Query("""
                SELECT COUNT(ap)
                FROM ClosedRecruitment cr
                  JOIN cr.recruitmentAnalytics ra
                  JOIN ra.joinedUsers ap
                WHERE cr.id = :closedRecruitmentId
            """)
    int countTotalRecruited(@Param("closedRecruitmentId") UUID closedRecruitmentId);

}

