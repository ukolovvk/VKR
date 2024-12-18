package ru.ssau.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ukolov-victor
 */

@Configuration
public class MinioConfig {

    @Value("${s3.url}")
    private String url;

    @Value("${s3.user}")
    private String user;

    @Value("${s3.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(user, password)
                .build();
    }
}
