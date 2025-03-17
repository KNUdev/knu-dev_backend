package ua.knu.knudev.teammanager.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.teammanager.domain.ClosedRecruitment;
import ua.knu.knudev.teammanager.domain.QClosedRecruitment;

import java.util.Arrays;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface ClosedRecruitmentRepository extends JpaRepository<ClosedRecruitment, UUID> {

    QClosedRecruitment qClosedRecruitment = QClosedRecruitment.closedRecruitment;

    @Query("""
                SELECT COUNT(ap)
                FROM ClosedRecruitment cr
                  JOIN cr.recruitmentAnalytics ra
                  JOIN ra.joinedUsers ap
                WHERE cr.id = :closedRecruitmentId
            """)
    int countTotalRecruited(@Param("closedRecruitmentId") UUID closedRecruitmentId);

    default Page<ClosedRecruitment> getAllClosedRecruitmentsByFilter(Pageable pageable, String title, Expertise expertise) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (StringUtils.isNotBlank(title)) {
            Arrays.stream(title.split("\\s+"))
                    .map(qClosedRecruitment.name::containsIgnoreCase)
                    .reduce(BooleanExpression::and)
                    .ifPresent(predicate::and);
        }
        if (expertise != null) {
            predicate.and(qClosedRecruitment.expertise.eq(expertise));
        }

        JPAQuery<ClosedRecruitment> query = getQueryFactory().selectFrom(qClosedRecruitment)
                .where(predicate)
                .orderBy(qClosedRecruitment.closedAt.desc())
                .offset(pageable.isUnpaged() ? 0 : pageable.getOffset())
                .limit(pageable.isUnpaged() ? Integer.MAX_VALUE : pageable.getPageSize());

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }


}

