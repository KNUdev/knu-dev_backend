package ua.knu.knudev.fileservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.rest.api.PDFServiceApi;
import ua.knu.knudev.rest.folder.FileFolderProperties;
import ua.knu.knudev.rest.folder.PdfFolder;
import ua.knu.knudev.rest.subfolder.PdfSubfolder;

import java.util.Set;

@Service
public class PDFService extends FileService implements PDFServiceApi {
    private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = Set.of("pdf");

    public PDFService(FileUploadAdapter fileUploadAdapter) {
        super(fileUploadAdapter);
    }

    @Override
    public String uploadFile(MultipartFile file, String filename, PdfSubfolder subfolder) {
        checkFileExtensionAllowance(file, ALLOWED_DOCUMENT_EXTENSIONS);
        return uploadFile(file, filename, getFolderProperties(subfolder));
    }

    @Override
    public String uploadFile(MultipartFile file, PdfSubfolder subfolder) {
        checkFileExtensionAllowance(file, ALLOWED_DOCUMENT_EXTENSIONS);

        String filename = generateRandomUUIDFilename(file);
        return uploadFile(file, filename, getFolderProperties(subfolder));
    }

    @Override
    public boolean existsByFilename(String filename, PdfSubfolder subfolder) {
        return existsByFilename(filename, getFolderProperties(subfolder));
    }

    private FileFolderProperties<PdfSubfolder> getFolderProperties(PdfSubfolder pdfSubfolder) {
        return FileFolderProperties.builder(PdfFolder.INSTANCE)
                .subfolder(pdfSubfolder)
                .build();
    }

}
