package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Double> {

    boolean existsById(Double id);

}
