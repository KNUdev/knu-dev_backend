package ua.knu.knudev.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.knu.knudev.taskmanager.domain.Task;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
