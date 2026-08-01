package com.epam.resources;

import java.nio.charset.StandardCharsets;

import com.epam.common.controller.ExceptionControllerAdvice;
import com.epam.resource.controller.Mp3Controller;
import com.epam.resource.exception.ResourceNotFoundException;
import com.epam.resource.service.Mp3Service;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

public class HttpBaseContractTest {

    private final Mp3Service mp3Service = Mockito.mock(Mp3Service.class);

    @BeforeEach
    void setup() {
        var controller = new Mp3Controller(mp3Service);
        RestAssuredMockMvc.standaloneSetup(
            MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
        );

        when(mp3Service.download("1")).thenReturn("MP3-BINARY-PLACEHOLDER".getBytes(StandardCharsets.UTF_8));
        when(mp3Service.download("999")).thenThrow(new ResourceNotFoundException(999L));
    }
}
