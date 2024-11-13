package ua.knu.knudev.fileservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.knu.knudev.fileservice.adapter.FileUploadAdapter;
import ua.knu.knudev.fileserviceapi.api.PDFServiceApi;
import ua.knu.knudev.fileserviceapi.folder.FileFolderProperties;
import ua.knu.knudev.fileserviceapi.folder.PdfFolder;
import ua.knu.knudev.fileserviceapi.subfolder.PdfSubfolder;

@Service
public class PDFService extends FileService implements PDFServiceApi {

    public PDFService(FileUploadAdapter fileUploadAdapter) {
        super(fileUploadAdapter);
    }

    @Override
    public String uploadFile(MultipartFile file, PdfSubfolder subfolder) {
        return uploadFile(file, getProperties(subfolder));
    }

    @Override
    public boolean existsByFilename(String filename, PdfSubfolder subfolder) {
        return existsByFilename(filename, getProperties(subfolder));
    }

    private FileFolderProperties<PdfSubfolder> getProperties(PdfSubfolder pdfSubfolder) {
        return FileFolderProperties.builder(PdfFolder.INSTANCE)
                .subfolder(pdfSubfolder)
                .build();
    }

}
