package ua.knu.knudev.intergrationtests;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ua.knu.knudev.intergrationtests.config.IntegrationTestsConfig;

@SpringBootTest(classes = IntegrationTestsConfig.class)
@ActiveProfiles("test")
public class TestSubmissionServiceIntegrationTest {

}
