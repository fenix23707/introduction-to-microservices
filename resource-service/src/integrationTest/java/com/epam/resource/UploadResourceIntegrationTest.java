package com.epam.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;

import com.epam.resource.config.TestContainersConfig;
import com.epam.resource.config.TestKafkaConfig;
import com.epam.resource.config.TestKafkaConfig.KafkaTestEventCollector;
import com.epam.resource.config.property.AwsS3Properties;
import com.epam.resource.dto.Mp3UploadResponse;
import com.epam.resource.repository.Mp3Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
@Import({TestContainersConfig.class, TestKafkaConfig.class})
public class UploadResourceIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private AwsS3Properties awsS3Properties;

    @Autowired
    private Mp3Repository mp3Repository;

    @Autowired
    private KafkaTestEventCollector kafkaTestEventCollector;

    @BeforeEach
    void setUp() {
        createBucketIfMissing();
    }

    private void createBucketIfMissing() {
        try {
            s3Client.headBucket(builder -> builder.bucket(awsS3Properties.getBucketName()));
        } catch (NoSuchBucketException ex) {
            s3Client.createBucket(builder -> builder.bucket(awsS3Properties.getBucketName()));
        }
    }

    @Test
    void shouldSuccessfullyUploadResource() throws Exception {
        var mp3Bytes = new ClassPathResource(
            "data/UploadResourceIntegrationTest/shouldSuccessfullyUploadResource/valid-sample.mp3")
            .getContentAsByteArray();

        var responseJson = mockMvc.perform(post("/resources")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .content(mp3Bytes))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var uploadResponse = objectMapper.readValue(responseJson, Mp3UploadResponse.class);
        assertThat(uploadResponse.id()).isNotNull();

        var entity = mp3Repository.findById(uploadResponse.id());
        assertThat(entity).isPresent();
        assertThat(entity.get().getBucket()).isEqualTo(awsS3Properties.getBucketName());
        assertThat(entity.get().getObjectKey()).isNotNull();

        var storedBytes = s3Client.getObject(builder -> builder
                .bucket(entity.get().getBucket())
                .key(entity.get().getObjectKey().toString()))
            .readAllBytes();
        assertThat(storedBytes).isEqualTo(mp3Bytes);

        await().atMost(Duration.ofSeconds(3))
            .untilAsserted(() -> {
                assertThat(kafkaTestEventCollector.getEvents()).hasSize(1);
                assertThat(kafkaTestEventCollector.getEvents().getFirst().resourceId()).isEqualTo(uploadResponse.id());
            });
    }
}
