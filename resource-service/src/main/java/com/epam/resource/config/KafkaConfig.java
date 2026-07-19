package com.epam.resource.config;

import com.epam.resource.config.property.KafkaSongProperties;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
}
