package com.epam.resource.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgreSQLContainer() {
        var container = new PostgreSQLContainer("postgres:18.3-alpine")
            .withInitScript("db/init.sql");
        container.start();
        return container;
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        var container = new KafkaContainer(DockerImageName.parse("apache/kafka:4.3.1"));
        container.start();
        return container;
    }


    static final LocalStackContainer LOCAL_STACK_CONTAINER =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:community-archive"))
            .withServices("s3");

    static {
        LOCAL_STACK_CONTAINER.start();
    }

    @Bean
    LocalStackContainer localStackContainer() {
        return LOCAL_STACK_CONTAINER;
    }

    @Bean
    DynamicPropertyRegistrar awsPropertiesRegistrar() {
        return registry -> {
            registry.add("aws.s3.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
            registry.add("aws.s3.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
            registry.add("aws.s3.region", LOCAL_STACK_CONTAINER::getRegion);
            registry.add("aws.s3.bucket-name", () -> "test-bucket");
            registry.add("aws.s3.endpoint", () -> LOCAL_STACK_CONTAINER.getEndpoint().toString());

            // Avoid attempting real Eureka registration/discovery during tests.
            registry.add("eureka.client.enabled", () -> false);
        };
    }
}
