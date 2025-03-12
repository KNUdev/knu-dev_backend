package ua.knu.knudev.knudevrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.knu.knudev.educationapi.request.SprintAdjustmentRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sprint")
public class AdminSprintController {


    @PatchMapping("/adjust")
    public void adjustSprintsDeadlines(@RequestBody List<SprintAdjustmentRequest> sprintsAdjustments) {

    }
}
