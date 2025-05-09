package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role_promotion_conditions", schema = "team_management")
@Builder
public class RolePromotionConditions {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private Integer toPremasterProjectQuantity;

    @Column(nullable = false)
    private Integer toPremasterCommitsQuantity;

    @Column(nullable = false)
    private Integer toMasterProjectQuantity;

    @Column(nullable = false)
    private Integer toMasterCommitsQuantity;

    @Column(nullable = false)
    private Integer toMasterCreatedCampusTasksQuantity;

    @Column(nullable = false)
    private Integer toMasterMentoredSessionsQuantity;

    @Column(nullable = false)
    private Integer toTechLeadCreatedCampusTasksQuantity;

    @Column(nullable = false)
    private Integer toTechLeadCommitsQuantity;

    @Column(nullable = false)
    private Boolean wasSupervisor;

    @Column(nullable = false)
    private Boolean wasArchitect;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean singleton;
}