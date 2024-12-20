package ua.knu.knudev.teammanager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import ua.knu.knudev.knudevcommon.constant.RecruitmentStatus;
import ua.knu.knudev.knudevcommon.constant.RecruitmentType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "team_management", name = "recruitment")
@Builder
public class Recruitment {

    @Id
    @Column(updatable = false, nullable = false)
    private Integer recruitmentNumber;

    @Column(nullable = false)
    private String recruitmentName;

    @Column(nullable = false)
    private RecruitmentType recruitmentType;

    @Column(updatable = false, nullable = false)
    private LocalDateTime recruitmentStartDateTime;

    @Column(updatable = false)
    private LocalDateTime recruitmentEndDateTime;

    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus;

    @Column
    private Integer recruitedPeopleNumber;

}
