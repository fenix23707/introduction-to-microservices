package com.epam.resource.config;

import com.epam.resource.config.property.AwsS3Properties;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

    @Bean
    public S3Client s3Client(@NonNull AwsS3Properties properties) {
        return S3Client.builder()
            .endpointOverride(properties.getEndpoint())
            .region(properties.getRegion())
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
            ))
            .forcePathStyle(true)
            .build();
    }
}
