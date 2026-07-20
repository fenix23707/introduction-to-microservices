package com.epam.resource.service;

import java.util.List;

import com.epam.resource.dto.S3Path;

public interface Mp3FileStorage {
    S3Path save(byte[] bytes);

    byte[] getByPath(S3Path path);

    void deleteAll(List<S3Path> paths);
}
