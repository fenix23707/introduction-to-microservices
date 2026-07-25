package com.epam.resource.config;

import com.epam.resource.config.property.KafkaSongProperties;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.retry.RetryTemplate;

@TestConfiguration
public class Mp3ServiceTestConfig {

    @Bean
    KafkaSongProperties kafkaSongProperties() {
        return new KafkaSongProperties("songs");
    }

    @Bean
    RetryTemplate kafkaRetryTemplate() {
        return new RetryTemplate();
    }

    @Bean
    RetryTemplate httpRetryTemplate() {
        return new RetryTemplate();
    }
}
