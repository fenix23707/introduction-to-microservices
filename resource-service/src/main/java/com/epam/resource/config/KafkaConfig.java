package com.epam.resource.config;

import java.time.Duration;

import com.epam.resource.config.property.KafkaSongProperties;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic songTopic(KafkaSongProperties kafkaSongProperties) {
        return TopicBuilder.name(kafkaSongProperties.getName())
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public RetryTemplate kafkaRetryTemplate() {
       var retryPolicy = RetryPolicy.builder()
           .maxRetries(3)
           .delay(Duration.ofSeconds(1))
           .multiplier(2)
           .includes(KafkaException.class)
           .build();
       return new RetryTemplate(retryPolicy);
    }
}
