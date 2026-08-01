package com.epam.e2e.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.Map;

import com.epam.e2e.client.PlatformApiClient;
import com.epam.e2e.client.RawHttpResponse;
import com.epam.e2e.dto.Mp3UploadResponse;
import com.epam.e2e.dto.SongMetadataDto;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

public class UploadSongSteps {

    private static final Duration PROCESSING_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);

    @Autowired
    private PlatformApiClient apiClient;

    @Autowired
    private ObjectMapper objectMapper;

    private byte[] fileBytes;
    private String contentType;
    private RawHttpResponse uploadResponse;
    private Long resourceId;

    @Before
    public void resetState() {
        fileBytes = null;
        contentType = null;
        uploadResponse = null;
        resourceId = null;
    }

    @Given("the platform services are up and running")
    public void thePlatformServicesAreUpAndRunning() {
        await().atMost(Duration.ofSeconds(60))
            .pollInterval(POLL_INTERVAL)
            .until(() -> apiClient.isResourceServiceReachable() && apiClient.isSongServiceReachable());
    }

    @Given("a valid MP3 file {string}")
    public void aValidMp3File(String fileName) throws Exception {
        fileBytes = new ClassPathResource("data/" + fileName).getContentAsByteArray();
        contentType = "audio/mpeg";
    }

    @Given("an invalid file {string} with content type {string}")
    public void anInvalidFile(String fileName, String requestedContentType) {
        fileBytes = ("not a real %s file".formatted(fileName)).getBytes();
        contentType = requestedContentType;
    }

    @When("I upload the file to the resource service")
    public void iUploadTheFile() {
        uploadResponse = apiClient.uploadMp3(fileBytes, contentType);
    }

    @Then("the upload should be accepted and a resource id returned")
    public void theUploadShouldBeAccepted() throws Exception {
        assertThat(uploadResponse.isSuccessful())
            .as("upload response body: %s", uploadResponse.body())
            .isTrue();

        var response = objectMapper.readValue(uploadResponse.body(), Mp3UploadResponse.class);
        assertThat(response.id()).isNotNull();
        resourceId = response.id();
    }

    @Then("the song metadata should eventually be available with:")
    public void theSongMetadataShouldEventuallyBeAvailable(DataTable dataTable) {
        Map<String, String> expected = dataTable.asMap(String.class, String.class);

        await().atMost(PROCESSING_TIMEOUT)
            .pollInterval(POLL_INTERVAL)
            .untilAsserted(() -> {
                var response = apiClient.getSongMetadata(resourceId);
                assertThat(response.isSuccessful())
                    .as("song metadata response body: %s", response.body())
                    .isTrue();

                var metadata = objectMapper.readValue(response.body(), SongMetadataDto.class);
                assertThat(metadata.id()).isEqualTo(resourceId);
                expected.forEach((field, expectedValue) -> assertThat(actualValue(metadata, field))
                    .as("field '%s'", field)
                    .isEqualTo(expectedValue));
            });
    }

    @Then("the upload should be rejected with a client error")
    public void theUploadShouldBeRejectedWithClientError() {
        assertThat(uploadResponse.isClientError())
            .as("upload response body: %s", uploadResponse.body())
            .isTrue();
    }

    private String actualValue(SongMetadataDto metadata, String field) {
        return switch (field) {
            case "name" -> metadata.name();
            case "artist" -> metadata.artist();
            case "album" -> metadata.album();
            case "year" -> metadata.year();
            case "duration" -> metadata.duration();
            default -> throw new IllegalArgumentException("Unknown song metadata field: " + field);
        };
    }
}
