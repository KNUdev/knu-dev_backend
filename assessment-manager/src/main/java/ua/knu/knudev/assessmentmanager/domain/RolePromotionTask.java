package ua.knu.knudev.assessmentmanager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        schema = "assessment_management",
        name = "role_promotion_task",
        indexes = {
                @Index(columnList = "target_technical_role", name = "idx_target_technical_role")
        }
)
@SuperBuilder
public class RolePromotionTask extends BaseTask {
    @Enumerated(EnumType.STRING)
    @Column(name = "target_technical_role", nullable = false)
    private AccountTechnicalRole targetTechnicalRole;

    @Column(name = "creator_account_email", nullable = false)
    private String creatorAccountEmail;
}
