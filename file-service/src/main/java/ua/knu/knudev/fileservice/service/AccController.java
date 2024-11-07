package ua.knu.knudev.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.api.ImageServiceApi;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

@RestController
@RequiredArgsConstructor
public class AccController {

    private final ImageServiceApi imageServiceApi;
    private final PDFServiceApi pdfServiceApi;

    @PostMapping("/images")
    public String test1(@RequestBody MultipartFile file) {
        return imageServiceApi.uploadFile(file, ImageSubfolder.TASK_PICTURES);
    }

    @PostMapping("/pdfs")
    public String test2(@RequestBody MultipartFile file) {
        return pdfServiceApi.uploadFile(file, PdfSubfolder.REQUIREMENTS);
    }
}
