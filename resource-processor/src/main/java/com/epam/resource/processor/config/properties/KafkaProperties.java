package com.epam.resource.processor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.kafka.topic")
public record KafkaProperties(
    KafkaTopicProperties song,
    KafkaTopicProperties songCreated
) {

}
