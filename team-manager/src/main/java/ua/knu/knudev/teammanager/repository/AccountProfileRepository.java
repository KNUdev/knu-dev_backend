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
import ua.knu.knudev.knudevcommon.constant.FilterOptions;
import ua.knu.knudev.teammanager.domain.AccountProfile;
import ua.knu.knudev.teammanager.domain.QAccountProfile;

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

    List<AccountProfile> findAllByEmail(String email);

    default Page<AccountProfile> findAllAccountsByFilters(Map<FilterOptions, Object> filters, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

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

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    private static Optional<BooleanExpression> getFilterPredicate(FilterOptions key, Object value) {
        Map<FilterOptions, BiFunction<QAccountProfile, Object, BooleanExpression>> filterMap = Map.of(
                FilterOptions.USER_INITIALS_AND_EMAIL, (profile, val) -> {
                    String[] parts = val.toString().trim().split("\\s+");
                    BooleanExpression predicate = null;
                    for (String part : parts) {
                        BooleanExpression predicateExpression = profile.email.containsIgnoreCase(part)
                                .or(profile.firstName.containsIgnoreCase(part))
                                .or(profile.lastName.containsIgnoreCase(part))
                                .or(profile.middleName.containsIgnoreCase(part));
                        predicate = (predicate == null) ? predicateExpression : predicate.or(predicateExpression);
                    }
                    return predicate;
                },
                FilterOptions.REGISTRATION_DATE, (profile, val) -> profile.registrationDate.eq((LocalDateTime) val),
                FilterOptions.EXPERTISE, (profile, val) -> profile.expertise.eq(Enum.valueOf(Expertise.class, val.toString())),
                FilterOptions.DEPARTMENT, (profile, val) -> profile.department.name.enName.eq(val.toString())
                        .or(profile.department.name.ukName.eq(val.toString())),
                FilterOptions.SPECIALTY, (profile, val) -> profile.specialty.name.enName.eq(val.toString())
                        .or(profile.specialty.name.ukName.eq(val.toString())),
                FilterOptions.TECHNICAL_ROLE, (profile, val) -> profile.technicalRole.eq(Enum.valueOf(AccountTechnicalRole.class, val.toString()))
//                TODO add more filters
        );

        return Optional.ofNullable(filterMap.get(key)).map(func -> func.apply(accountProfile, value));
    }
}
