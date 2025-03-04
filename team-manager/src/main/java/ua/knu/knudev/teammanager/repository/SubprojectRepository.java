package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.QSubproject;
import ua.knu.knudev.teammanager.domain.Subproject;

import java.util.List;
import java.util.UUID;

import static ua.knu.knudev.knudevcommon.config.QEntityManagerUtil.getQueryFactory;

public interface SubprojectRepository extends JpaRepository<Subproject, UUID> {

    QSubproject qSubproject = QSubproject.subproject;

    default List<Subproject> retrieveAllSubprojectsByDeveloperIn(UUID developerId) {
        return getQueryFactory().selectFrom(qSubproject)
                .where(qSubproject.allDevelopers.any().id.accountId.eq(developerId))
                .fetch();
    }

}
