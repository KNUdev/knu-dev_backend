package ua.knu.knudev.education.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.knu.knudev.educationapi.api.EducationProgramApi;
import ua.knu.knudev.educationapi.dto.EducationProgramDto;
import ua.knu.knudev.educationapi.request.EducationProgramCreationRequest;

@Service
@RequiredArgsConstructor
public class EducationProgramCreationService implements EducationProgramApi {
    private final EducationProgramRequestCoherenceValidator inputReqCoherenceValidator;
//    private final PDFServiceApi pdfServiceApi;

    @Override
    @Transactional
    public EducationProgramDto create(EducationProgramCreationRequest programCreationRequest) {
        inputReqCoherenceValidator.validateProgramCreationRequest(programCreationRequest);


        //todo validate before all
        /*
            Validation
            1. Validate the orders. Validate first order of topic sections, topics, topic
            And eventually i must form the sequence of all topics

            2. Validate the size of each file. Perhaps directly on frontned. Max size - 1024 * 5 KB
            3. Some stuff with tests
         */

        //todo create the program sections, topics, topics
        //todo Create files with tasks
        //todo create tests
        //todo create relations

        return null;
    }

}
