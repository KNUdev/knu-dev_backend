package ua.knu.knudev.intergrationtests;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EducationProgramCreationServiceTest {

    //todo write
}
