package com.epam.e2e;

import com.epam.e2e.config.property.E2eServicesProperties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = "com.epam.e2e")
@EnableConfigurationProperties(E2eServicesProperties.class)
public class E2eTestApplication {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestClient resourceServiceClient(E2eServicesProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.resourceService().baseUrl())
            .build();
    }

    @Bean
    public RestClient songServiceClient(E2eServicesProperties properties) {
        return RestClient.builder()
            .baseUrl(properties.songService().baseUrl())
            .build();
    }
}
