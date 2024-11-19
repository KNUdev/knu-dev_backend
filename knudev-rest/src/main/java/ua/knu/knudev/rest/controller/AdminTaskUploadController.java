package ua.knu.knudev.rest.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.taskmanagerapi.api.TaskUploadAPI;

@RestController
@RequestMapping("/admin/task/upload")
@RequiredArgsConstructor
public class AdminTaskUploadController {
    private final TaskUploadAPI taskUploadAPI;

    @PostMapping(value = "/campus/{role}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/plain;charset=UTF-8"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadTaskForRole(@PathVariable("role") String accountRole,
                                    @RequestParam("taskFile") @Valid @NotNull MultipartFile taskFile) {
        return taskUploadAPI.uploadTaskForRole(accountRole, taskFile);
    }

}
