package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.AccountProfile;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, Integer> {
    boolean existsByEmail(String email);
}
