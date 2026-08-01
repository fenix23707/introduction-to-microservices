package com.epam.resource.config;

import com.epam.common.api.song.SongApi;
import com.epam.common.controller.ExceptionControllerAdvice;
import com.epam.common.service.IdsAsCsvParser;
import com.epam.resource.controller.Mp3Controller;
import com.epam.resource.dto.kafka.ResourceUploadEvent;
import com.epam.resource.repository.Mp3Repository;
import com.epam.resource.service.Mp3FileStorage;
import com.epam.resource.service.Mp3Service;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@WebMvcTest(controllers = Mp3Controller.class)
@Import({Mp3Service.class, ExceptionControllerAdvice.class, Mp3ServiceTestConfig.class})
public class CucumberSpringConfiguration {

    @MockitoBean
    Mp3Repository mp3Repository;

    @MockitoBean
    Mp3FileStorage mp3FileStorage;

    @MockitoBean
    KafkaTemplate<String, ResourceUploadEvent> kafkaTemplate;

    @MockitoBean
    IdsAsCsvParser idsAsCsvParser;

    @MockitoBean
    SongApi songApi;
}
