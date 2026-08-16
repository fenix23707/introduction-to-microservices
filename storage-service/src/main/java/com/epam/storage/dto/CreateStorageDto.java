package com.epam.storage.dto;

public record CreateStorageDto(
    StorageType storageType,
    String bucket,
    String path
) {
}
