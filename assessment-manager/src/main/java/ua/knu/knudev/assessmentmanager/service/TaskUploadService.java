package ua.knu.knudev.assessmentmanager.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.AccountTechnicalRole;
import ua.knu.knudev.knudevcommon.constant.LearningUnit;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.assessmentmanager.config.TaskFileConfigProperties;
import ua.knu.knudev.assessmentmanagerapi.api.TaskUploadAPI;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TaskUploadService implements TaskUploadAPI {

    private final TaskService taskService;
    private final PDFServiceApi pdfServiceApi;
    private final TaskFileConfigProperties taskFileConfigProperties;

    @Override
    public String uploadTaskForRole(String stringAccountTechnicalRole, MultipartFile file) {
        checkFileValidity(file);

        //todo check if string value can be casted to enum
        AccountTechnicalRole accountRole = AccountTechnicalRole.valueOf(stringAccountTechnicalRole);
        PdfSubfolder subfolder = getSubfolderByTechnicalRole(accountRole);
        String savedTaskFilename = taskService.create(file.getOriginalFilename(), accountRole);

        return pdfServiceApi.uploadFile(file, savedTaskFilename, subfolder);
    }

    @Override
    public String uploadTaskForEducationProgram(LearningUnit learningUnit, MultipartFile file) {
        return "";
    }

    private PdfSubfolder getSubfolderByTechnicalRole(AccountTechnicalRole role) {
        return switch (role) {
            case INTERN -> PdfSubfolder.INTERN_ROLE_TASKS;
            case DEVELOPER -> PdfSubfolder.DEVELOPER_ROLE_TASKS;
            case PREMASTER -> PdfSubfolder.PREMASTER_ROLE_TASKS;
            case MASTER -> PdfSubfolder.MASTER_ROLE_TASKS;
            case TECHLEAD -> PdfSubfolder.TECHLEAD_ROLE_TASKS;
        };
    }

    private void checkFileValidity(MultipartFile file) {
        validateFilename(file);
        checkMaxTaskSize(file);
    }

    private void validateFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String noExtensionFilename = removeExtension(filename);

        //todo build this role pattern based on enum values
        final String ROLE_PATTERN = "Developer|Techlead";
        final String CAMEL_CASE_PATTERN = "([A-Z][a-z0-9]+)+";
        final String FILENAME_PATTERN = "^Task_(" + ROLE_PATTERN + ")_(" + CAMEL_CASE_PATTERN + ")$";

        Pattern pattern = Pattern.compile(FILENAME_PATTERN);
        Matcher matcher = pattern.matcher(noExtensionFilename);

        if (!matcher.matches()) {
            throw new FileException(
                    String.format("Invalid filename naming: %s", filename)
            );
        }
    }

    private String removeExtension(String filename) {
        if (StringUtils.isEmpty(filename)) {
            throw new TaskException("Filename is empty", HttpStatus.BAD_REQUEST);
        }

        int dotIndex = filename.indexOf(".");
        return filename.substring(0, dotIndex);
    }

    private void checkMaxTaskSize(MultipartFile file) {
        //todo refactor naming here
        long fileSizeInBytes = file.getSize() / (1024 ^ 3);
        final int MAX_FILE_SIZE = taskFileConfigProperties.maximumSizeInKilobytes();
        if (fileSizeInBytes > MAX_FILE_SIZE) {
            throw new FileException("File is too large");
        }
    }

}
