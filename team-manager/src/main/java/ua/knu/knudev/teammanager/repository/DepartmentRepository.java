package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.knu.knudev.teammanager.domain.Department;
import ua.knu.knudev.teammanager.domain.Specialty;

import java.util.Set;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    @Query("""
        SELECT s
        FROM Department d
        JOIN d.specialties s
        WHERE d.id = :id
    """)
    Set<Specialty> findSpecialtiesByDepartmentId(@Param("id") UUID id);

    boolean existsByName_En(String enName);

    boolean existsByName_Uk(String ukName);
}
