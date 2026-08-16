package com.epam.resource.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.kafka.topic")
public record KafkaProperties(
    KafkaTopicProperties song,
    KafkaTopicProperties songCreated
) {

}
