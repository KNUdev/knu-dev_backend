package ua.knu.knudev.teammanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.knu.knudev.teammanager.domain.ProjectReleaseInfo;

import java.util.UUID;

public interface ProjectReleaseInfoRepository extends JpaRepository<ProjectReleaseInfo, UUID> {
}
