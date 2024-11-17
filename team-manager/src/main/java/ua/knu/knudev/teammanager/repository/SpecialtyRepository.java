package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.Specialty;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Double> {

    boolean existsByCodeName(Double codeName);
    List<Specialty> findSpecialtiesByCodeNameIn(List<Double> codeNames);

}
