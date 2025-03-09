package ua.knu.knudev.education.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ua.knu.knudev.education.domain.program.BaseLearningUnit;
import ua.knu.knudev.knudevcommon.constant.Expertise;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "program", schema = "education")
@SuperBuilder
public class EducationProgram extends BaseLearningUnit {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Expertise expertise;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

}
