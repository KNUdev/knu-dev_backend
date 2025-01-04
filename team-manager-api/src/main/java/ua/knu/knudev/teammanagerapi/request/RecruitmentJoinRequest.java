package ua.knu.knudev.teammanagerapi.request;

import java.util.UUID;

public record RecruitmentJoinRequest(UUID accountId, UUID activeRecruitmentId) {
}
