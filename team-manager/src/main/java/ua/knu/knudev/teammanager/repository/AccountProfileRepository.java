package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.AccountProfile;

import java.util.List;
import java.util.Optional;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, Integer> {
    boolean existsByEmail(String email);
    Optional<AccountProfile> findByEmail(String email);
    List<AccountProfile> findAllByEmail(String email);
}
