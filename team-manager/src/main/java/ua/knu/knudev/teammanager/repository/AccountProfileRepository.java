package ua.knu.knudev.teammanager.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.constant.KNUdevUnit;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.QAccountProfile;
import ua.knu.knudev.teammanagerapi.constant.AccountsCriteriaFilterOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, UUID> {

    QAccountProfile accountProfile = QAccountProfile.accountProfile;

    boolean existsByEmail(String email);

    Optional<AccountProfile> findByEmail(String email);

    Optional<AccountProfile> findAccountProfileByGithubAccountUsername(String githubAccountNickname);

    List<AccountProfile> findAllByEmail(String email);

    Page<AccountProfile> findAllByUnit(KNUdevUnit unit, Pageable pageable);

    default Page<AccountProfile> findAllAccountsByFilters(Map<AccountsCriteriaFilterOption, Object> filters, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();
        Object universityStudyYears = filters.get(AccountsCriteriaFilterOption.UNIVERSITY_STUDY_YEARS);

        filters.forEach((key, value) -> {
            if (value != null) {
                getFilterPredicate(key, value).ifPresent(predicate::and);
            }
        });

        JPAQuery<AccountProfile> query = getQueryFactory()
                .selectFrom(accountProfile)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.isUnpaged() ? Integer.MAX_VALUE : pageable.getPageSize())
                .orderBy(accountProfile.registrationDate.asc(), accountProfile.technicalRole.asc());

        if (universityStudyYears != null) {
            List<AccountProfile> filteredAccounts = query.fetch().stream()
                    .filter(account -> account.getCurrentYearOfStudy().equals(universityStudyYears))
                    .toList();

            return PageableExecutionUtils.getPage(filteredAccounts, pageable,query::fetchCount);
        }

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    private static Optional<BooleanExpression> getFilterPredicate(AccountsCriteriaFilterOption key, Object value) {
        Map<AccountsCriteriaFilterOption, BiFunction<QAccountProfile, Object, BooleanExpression>> filterMap = Map.of(
                AccountsCriteriaFilterOption.USER_INITIALS_OR_GITHUB_OR_EMAIL, (profile, val) -> {
                    String[] parts = val.toString().trim().split("\\s+");
                    BooleanExpression predicate = null;
                    for (String part : parts) {
                        BooleanExpression predicateExpression = profile.email.containsIgnoreCase(part)
                                .or(profile.githubAccountUsername.containsIgnoreCase(part))
                                .or(profile.firstName.containsIgnoreCase(part))
                                .or(profile.lastName.containsIgnoreCase(part))
                                .or(profile.middleName.containsIgnoreCase(part));
                        predicate = (predicate == null) ? predicateExpression : predicate.or(predicateExpression);
                    }
                    return predicate;
                },
                AccountsCriteriaFilterOption.REGISTERED_BEFORE,
                (profile, val) -> profile.registrationDate.before((LocalDateTime) val),
                AccountsCriteriaFilterOption.REGISTERED_AT,
                (profile, val) -> profile.registrationDate.after((LocalDateTime) val),
                AccountsCriteriaFilterOption.EXPERTISE,
                (profile, val) -> profile.expertise.eq(Enum.valueOf(Expertise.class, val.toString())),
                AccountsCriteriaFilterOption.DEPARTMENT,
                (profile, val) -> profile.department.id.eq((UUID) val),
                AccountsCriteriaFilterOption.SPECIALTY,
                (profile, val) -> profile.specialty.codeName.eq((Double) val),
                AccountsCriteriaFilterOption.TECHNICAL_ROLE,
                (profile, val) -> profile.technicalRole.eq(Enum.valueOf(AccountTechnicalRole.class, val.toString())),
                AccountsCriteriaFilterOption.UNIT,
                (profile, val) -> profile.unit.eq(Enum.valueOf(KNUdevUnit.class, val.toString()))
        );

        return Optional.ofNullable(filterMap.get(key))
                .map(func -> func.apply(accountProfile, value));
    }
}
