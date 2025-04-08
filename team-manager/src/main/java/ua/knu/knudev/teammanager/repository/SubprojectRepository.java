package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Subproject;

import java.util.List;
import java.util.UUID;

public interface SubprojectRepository extends JpaRepository<Subproject, UUID> {

    List<Subproject> findAllByAccountId(UUID accountId);

}
