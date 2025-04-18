package ua.knu.knudev.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.education.domain.session.EducationSession;
import ua.knu.knudev.educationapi.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EducationSessionRepository extends JpaRepository<EducationSession, UUID> {

    @Modifying
    @Query("update EducationSession s set s.estimatedEndDate = :endDate where s.id = :id")
    void updateEstimatedEndDateById(@Param(value = "id") UUID id,
                                    @Param(value = "endDate") LocalDateTime estimatedEndDate);

    boolean existsByIdAndStatus(UUID id, SessionStatus status);

    @Query("SELECT s FROM EducationSession s WHERE :mentorId MEMBER OF s.mentorIds")
    Optional<List<EducationSession>> findAllByMentorId(UUID mentorId);
}
