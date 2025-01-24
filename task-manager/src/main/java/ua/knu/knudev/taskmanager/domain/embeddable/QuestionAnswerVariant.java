package ua.knu.knudev.taskmanager.domain.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerVariant {
    private Integer variantNumber;
    private String variantBody;
    private Boolean isCorrectAnswer;
}
