package ua.knu.knudev.education.repository;

import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.education.domain.session.QSprint;
import ua.knu.knudev.education.domain.session.Sprint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {
    List<Sprint> findAllBySession_Id(UUID sessionId);

    Sprint findBySession_IdAndOrderIndex(UUID sessionId, int orderIndex);

    default void updateSprintStartDates(UUID sessionId, int orderIndexParam, int daysToAdd) {
        QSprint sprint = QSprint.sprint;
        // Create a DateTimeExpression that adds daysToAdd to the current startDate.
        DateTimeExpression<LocalDateTime> newStartDate = Expressions.dateTimeTemplate(
                LocalDateTime.class,
                "({1} + ({0} * interval '1 day'))",
                daysToAdd,
                sprint.startDate
        );

        getQueryFactory().update(sprint)
                .set(sprint.startDate, newStartDate)
                .where(sprint.orderIndex.gt(orderIndexParam))
                .execute();
    }

    /*
        update education.sprint
        set sprint.durationInDays = newDurationInDays
        where sprint.id = (
            select sprint.id
            from education.sprint
            where s
        )
     */
}
