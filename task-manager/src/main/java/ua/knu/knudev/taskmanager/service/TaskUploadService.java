package ua.knu.knudev.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.taskmanagerapi.api.TaskUploadAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaskUploadService implements TaskUploadAPI {
    private final PDFServiceApi pdfServiceApi;
    @Value("${application.files.pdfs.tasks.maximum-size-in-kilobytes}")
    private Integer MAX_TASK_SIZE_IN_KILOBYTES;

    @Override
    public String uploadTaskForRole(AccountRole accountRole, MultipartFile file) {
        checkFileValidity(file);
        PdfSubfolder subfolder = getSubfolderByRole(accountRole);

        return pdfServiceApi.uploadFile(file, file.getOriginalFilename(), subfolder);
    }

    private PdfSubfolder getSubfolderByRole(AccountRole accountRole) {
        return switch (accountRole) {
            case DEVELOPER -> PdfSubfolder.DEVELOPER_ROLE_TASKS;
            case TEACHLEAD -> PdfSubfolder.TECHLEAD_ROLE_TASKS;
            default -> throw new IllegalArgumentException(
                    String.format("No tasks present for accountRole %s or accountRole %s does not exist",
                            accountRole, accountRole)
            );
        };
    }

    private void checkFileValidity(MultipartFile file) {
        validateFilename(file);
        checkMaxTaskSize(file);
    }

    private void validateFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();

        // Constants for roles and regex patterns
        final String ROLE_PATTERN = "Developer|Techlead";
        final String CAMEL_CASE_PATTERN = "([A-Z][a-z]+)+";
        final String FILENAME_PATTERN = "^Task_(" + ROLE_PATTERN + ")_(" + CAMEL_CASE_PATTERN + ")$";

        Pattern pattern = Pattern.compile(FILENAME_PATTERN);
        Matcher matcher = pattern.matcher(filename);

        if (!matcher.matches()) {
            throw new FileException(
                    String.format("Invalid filename naming: %s", filename)
            );
        }
    }


    private void checkMaxTaskSize(MultipartFile file) {
        long fileSizeInBytes = file.getSize() / (1024 ^ 3);
        if (fileSizeInBytes > MAX_TASK_SIZE_IN_KILOBYTES) {
            throw new FileException("File is too large");
        }
    }

}
