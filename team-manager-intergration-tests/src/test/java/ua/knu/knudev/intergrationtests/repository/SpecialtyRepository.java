package ua.knu.knudev.intergrationtests.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Specialty;

@Profile("test")
public interface SpecialtyRepository extends JpaRepository<Specialty, Double> {
}
