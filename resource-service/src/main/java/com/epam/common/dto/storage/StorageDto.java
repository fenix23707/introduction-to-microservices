package com.epam.common.dto.storage;

public record StorageDto(
    Long id,
    StorageType storageType,
    String bucket,
    String path
) {
}
