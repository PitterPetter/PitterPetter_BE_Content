package PitterPatter.loventure.content.global.infra.gcs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Google Cloud Storage 설정
 */
@Slf4j
@Configuration
public class GcsConfig {
    
    @Value("${gcs.project-id}")
    private String projectId;
    
    @Value("${gcs.bucket-name}")
    private String bucketName;
    
    @Value("${gcs.credentials-path}")
    private String credentialsPath;
    
    @Bean
    public Storage storage() throws IOException {
        log.info("Initializing GCS Storage with project: {}, bucket: {}", projectId, bucketName);
        
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            new FileInputStream(credentialsPath)
        );
        
        return StorageOptions.newBuilder()
            .setProjectId(projectId)
            .setCredentials(credentials)
            .build()
            .getService();
    }
    
    @Bean
    public String bucketName() {
        return bucketName;
    }
}

