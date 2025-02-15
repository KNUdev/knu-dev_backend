package ua.knu.knudev.knudevrest.config;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private Integer score;       // if scoring the task
    private String comment;      // mentor's feedback
    private boolean requestResend; // true if mentor asks for a re-send
}
