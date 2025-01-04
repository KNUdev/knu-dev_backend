package ua.knu.knudev.teammanagerapi.request;

import ua.knu.knudev.teammanagerapi.constant.RecruitmentCloseCause;

import java.util.UUID;

public record RecruitmentCloseRequest(UUID activeRecruitmentId, RecruitmentCloseCause closeCause) {
}
