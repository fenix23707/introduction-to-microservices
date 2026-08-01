package com.epam.resource.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.epam.common.dto.ExceptionDto;
import com.epam.resource.dto.Mp3UploadResponse;
import com.epam.resource.dto.S3Path;
import com.epam.resource.dto.kafka.ResourceUploadEvent;
import com.epam.resource.entity.Mp3Entity;
import com.epam.resource.repository.Mp3Repository;
import com.epam.resource.service.Mp3FileStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class Mp3UploadSteps {

    private static final Long STORED_ENTITY_ID = 42L;
    private static final String SONG_TOPIC = "songs";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mp3Repository mp3Repository;

    @Autowired
    private Mp3FileStorage mp3FileStorage;

    @Autowired
    private KafkaTemplate<String, ResourceUploadEvent> kafkaTemplate;

    private byte[] uploadedFileBytes;
    private MediaType uploadedFileContentType;
    private ResultActions resultActions;

    @Given("a valid MP3 file {string}")
    public void aValidMp3File(String fileName) throws IOException {
        uploadedFileBytes = new ClassPathResource("data/" + fileName).getContentAsByteArray();
        uploadedFileContentType = MediaType.parseMediaType("audio/mpeg");

        when(mp3FileStorage.save(any())).thenReturn(new S3Path("test-bucket", UUID.randomUUID()));
        doAnswer(invocation -> {
            Mp3Entity entity = invocation.getArgument(0);
            entity.setId(STORED_ENTITY_ID);
            return entity;
        }).when(mp3Repository).save(any());
        when(kafkaTemplate.send(eq(SONG_TOPIC), any())).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Given("an invalid file {string} with type {string}")
    public void anInvalidFile(String fileName, String contentType) {
        uploadedFileBytes = ("not a real %s file".formatted(fileName)).getBytes();
        uploadedFileContentType = MediaType.parseMediaType(contentType);
    }

    @When("I upload the file")
    public void iUploadTheFile() throws Exception {
        resultActions = mockMvc.perform(post("/resources")
            .contentType(uploadedFileContentType)
            .content(uploadedFileBytes));
    }

    @Then("the file should be stored in S3")
    public void theFileShouldBeStoredInS3() {
        verify(mp3FileStorage).save(uploadedFileBytes);
    }

    @Then("the resource metadata should be persisted in the database")
    public void theResourceMetadataShouldBePersistedInTheDatabase() throws Exception {
        var responseBody = resultActions.andReturn().getResponse().getContentAsString();
        var uploadResponse = objectMapper.readValue(responseBody, Mp3UploadResponse.class);

        assertThat(uploadResponse.id()).isEqualTo(STORED_ENTITY_ID);
        verify(mp3Repository).save(any(Mp3Entity.class));
    }

    @Then("a {string} event should be published to Kafka")
    public void anEventShouldBePublishedToKafka(String eventName) {
        assertThat(eventName).isEqualTo("SongUploaded");

        verify(kafkaTemplate).send(eq(SONG_TOPIC), eq(new ResourceUploadEvent(STORED_ENTITY_ID)));
    }

    @Then("the upload should be rejected with message {string}")
    public void theUploadShouldBeRejectedWithMessage(String expectedMessage) throws Exception {
        var response = resultActions.andReturn().getResponse();
        assertThat(response.getStatus()).isGreaterThanOrEqualTo(400);

        var exceptionDto = objectMapper.readValue(response.getContentAsString(), ExceptionDto.class);
        assertThat(exceptionDto.errorMessage()).contains(expectedMessage);
    }

    @Then("nothing should be stored in S3, the database, or Kafka")
    public void nothingShouldBeStoredAnywhere() {
        verifyNoInteractions(mp3Repository, mp3FileStorage, kafkaTemplate);
    }
}
