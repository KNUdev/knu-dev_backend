package ua.knu.knudev.fileservice.adapter.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "application.minio")
public class MinioProperties {
    private String url;
    private String accessKey;
    private String secretKey;
}
