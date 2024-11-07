package ua.knu.knudev.fileserviceapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.taskmanagerapi.api.TaskAPI;

@RestController("/admin/task")
@RequiredArgsConstructor
public class AdminTaskController {

//    private final


    @PostMapping("/upload/campus/developer-role")
    public void uploadTaskForDeveloperRole(@RequestBody MultipartFile task) {
        taskAPI.
    }
}
