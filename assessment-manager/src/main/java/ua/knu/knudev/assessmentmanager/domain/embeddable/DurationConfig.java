package ua.knu.knudev.assessmentmanager.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * ------------------------------------------------------------------------------------------
 * timeUnitPerTextCharacter -> Constant. Time unit for text(question and answer) in seconds
 * ------------------------------------------------------------------------------------------
 * extraTimePerCorrectAnswer -> Constant. Extra time added for each correct answer in seconds
 * ------------------------------------------------------------------------------------------
 * */

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class DurationConfig {
    @Column(nullable = false)
    private Integer timeUnitPerTextCharacter;

    @Column(nullable = false)
    private Integer extraTimePerCorrectAnswer;

}
