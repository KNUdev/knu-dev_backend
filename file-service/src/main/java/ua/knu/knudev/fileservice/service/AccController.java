package ua.knu.knudev.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileserviceapi.subfolder.ImageSubfolder;
import ua.knu.knudev.fileserviceapi.folder.PdfFolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;
import ua.knu.knudev.fileserviceapi.api.FileServiceApi;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.ImageFolder;

@RestController
@RequiredArgsConstructor
public class AccController {

    private final FileServiceApi fileServiceApi;

    @PostMapping("/images")
    public String test1(@RequestBody MultipartFile file) {
        FileFolderProperties<ImageSubfolder> imagesProps = FileFolderProperties.builder(ImageFolder.INSTANCE)
                .subfolder(ImageSubfolder.TASK_PICTURES)
                .build();
        return fileServiceApi.uploadFile(file, imagesProps);
    }

    @PostMapping("/pdfs")
    public String test2(@RequestBody MultipartFile file) {
        FileFolderProperties<PdfSubfolder> imagesProps = FileFolderProperties.builder(PdfFolder.INSTANCE)
                .subfolder(PdfSubfolder.REQUIREMENTS)
                .build();
        return fileServiceApi.uploadFile(file, imagesProps);
    }
}
