package com.epam.resource.dto;

import java.util.UUID;

import com.epam.resource.entity.Mp3Entity;

public record S3Path(String bucket, UUID key) {

    public static S3Path fromEntity(Mp3Entity it) {
        return new S3Path(it.getBucket(), it.getObjectKey());
    }
}
