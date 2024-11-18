package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.AccountProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, UUID> {
    boolean existsByEmail(String email);

    Optional<AccountProfile> findByEmail(String email);

    List<AccountProfile> findAllByEmail(String email);
}
