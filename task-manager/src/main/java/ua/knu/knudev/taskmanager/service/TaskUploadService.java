package ua.knu.knudev.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.knudevcommon.constant.AccountRole;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.taskmanagerapi.api.TaskUploadAPI;

@Service
@RequiredArgsConstructor
public class TaskUploadService implements TaskUploadAPI {
    private final static long MAX_FILE_SIZE_IN_MEGABYTES = 10 * 1024 * 1024;
    private final PDFServiceApi pdfServiceApi;


    private void checkMaxTaskSize(MultipartFile file) {
//        if(file.getSize() > MAX_FILE_SIZE_IN_MEGABYTES) {
//            throw
//        }
    }

    @Override
    public void uploadTaskForRole(AccountRole accountRole, MultipartFile file) {

//        performFileChecks(file);
        PdfSubfolder subfolder = getSubfolderByRole(accountRole);

        // Upload the file
        pdfServiceApi.uploadFile(file, subfolder);
    }

    private PdfSubfolder getSubfolderByRole(AccountRole accountRole) {
        return switch (accountRole) {
            case DEVELOPER -> PdfSubfolder.DEVELOPER_ROLE_TASKS;
            case TEACHLEAD -> PdfSubfolder.TECHLEAD_ROLE_TASKS;
            default -> throw new IllegalArgumentException(
                    String.format("No tasks present for accountRole %s or accountRole %s does not exist", accountRole, accountRole)
            );
        };
    }
}
