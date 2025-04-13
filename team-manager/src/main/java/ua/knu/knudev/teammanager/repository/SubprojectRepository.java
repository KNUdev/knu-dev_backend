package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.teammanager.domain.Subproject;

import java.util.List;
import java.util.UUID;

public interface SubprojectRepository extends JpaRepository<Subproject, UUID> {

    @Query("select s from Subproject s " +
            "inner join SubprojectAccount sa on s.id = sa.subproject.id " +
            "where sa.accountProfile.id = :accountId")
    List<Subproject> findAllByAccountId(@Param("accountId") UUID accountId);
}

