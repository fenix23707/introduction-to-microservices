package com.epam.resource.client;

import com.epam.common.api.storage.StorageApi;
import com.epam.common.dto.storage.StorageDto;
import com.epam.common.dto.storage.StorageType;
import com.epam.resource.config.property.AwsS3Properties;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class StorageClient implements StorageApi {

    private final StorageApi storageApi;
    private final AwsS3Properties awsS3Properties;

    @Override
    @CircuitBreaker(name = "storageService", fallbackMethod = "getStorageByTypeFallback")
    public StorageDto getStorageByType(StorageType type) {
        return storageApi.getStorageByType(type);
    }

    public StorageDto getStorageByTypeFallback(StorageType type, Throwable throwable) {
        log.error("Failed to get storage by type {}: {}", type, throwable.getMessage(), throwable);
        return new StorageDto(0L, type, awsS3Properties.getBucketName(), type.name().toLowerCase() + "/");
    }
}
