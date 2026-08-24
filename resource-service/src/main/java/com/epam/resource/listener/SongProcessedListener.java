package com.epam.resource.listener;

import com.epam.resource.dto.kafka.ResourceProcessedEvent;
import com.epam.resource.service.Mp3Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SongProcessedListener {

    private final Mp3Service mp3Service;

    @KafkaListener(topics = "${application.kafka.topic.song-created.name}", groupId = "${spring.application.name}")
    @SneakyThrows
    public void handleResourceProcessedEvent(ResourceProcessedEvent event) {
        log.info("Received ResourceProcessedEvent for resourceId: {}", event.resourceId());
        mp3Service.moveToPermanentStorage(event.resourceId());
    }
}
