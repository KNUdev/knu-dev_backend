package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.SubprojectAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubprojectAccountRepository extends JpaRepository<SubprojectAccount, UUID> {

    Optional<Set<SubprojectAccount>> findAllById_AccountId(UUID accountId);

}
