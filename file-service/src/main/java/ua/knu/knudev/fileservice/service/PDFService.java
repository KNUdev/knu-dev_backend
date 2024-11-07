package ua.knu.knudev.fileservice.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.exception.FileException;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.PdfFolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

import java.io.IOException;
import java.util.Set;

@Service
public class PDFService extends FileService implements PDFServiceApi {
    private final static long MAX_DOCUMENT_SIZE_IN_MEGABYTES = 10;
    private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = Set.of("pdf");

    public PDFService(FileUploadAdapter fileUploadAdapter) {
        super(fileUploadAdapter);
    }

    @Override
    public String uploadFile(MultipartFile file, PdfSubfolder subfolder) {
        checkFileExtensionAllowance(file, ALLOWED_DOCUMENT_EXTENSIONS);

        return uploadFile(file, getFolderProperties(subfolder));
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

    private void checkFileSize(MultipartFile file) {
        try {
            int fileSizeInMegabytes = file.getBytes().length / 1024 / 1024;
            if(fileSizeInMegabytes > MAX_DOCUMENT_SIZE_IN_MEGABYTES) {
                throw new FileException("File size is too big. Maximum allowed size is 2 megabytes");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
