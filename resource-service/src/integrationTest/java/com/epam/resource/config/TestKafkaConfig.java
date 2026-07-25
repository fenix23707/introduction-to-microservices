package com.epam.resource.config;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.epam.resource.dto.kafka.ResourceUploadEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    public KafkaTestEventCollector testEventCollector(ObjectMapper objectMapper) {
        return new KafkaTestEventCollector(objectMapper);
    }

    public static class KafkaTestEventCollector {
        private final List<ResourceUploadEvent> events = new CopyOnWriteArrayList<>();
        private final ObjectMapper objectMapper;

        public KafkaTestEventCollector(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public List<ResourceUploadEvent> getEvents() {
            return events;
        }

        @KafkaListener(topics = "${application.kafka.topic.song.name}", groupId = "test-collector")
        void collect(String rawEvent) throws JsonProcessingException {
            events.add(objectMapper.readValue(rawEvent, ResourceUploadEvent.class));
        }
    }
}