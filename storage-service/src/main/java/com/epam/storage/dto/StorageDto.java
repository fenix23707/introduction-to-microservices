package com.epam.storage.dto;

public record StorageDto(
    Long id,
    StorageType storageType,
    String bucket,
    String path
) {
}
