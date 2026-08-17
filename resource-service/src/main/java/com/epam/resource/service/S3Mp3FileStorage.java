package com.epam.resource.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import com.epam.common.api.storage.StorageApi;
import com.epam.common.dto.storage.StorageType;
import com.epam.resource.dto.S3Path;
import com.epam.resource.exception.storage.FileStorageDeleteException;
import com.epam.resource.exception.storage.FileStorageException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3Mp3FileStorage implements Mp3FileStorage {

    private final S3Client s3Client;
    @Qualifier("storageClient")
    private final StorageApi storageApi;

    @Override
    @Retryable(includes = S3Exception.class, multiplier = 2)
    public S3Path saveStaging(byte[] bytes) {
        var storage = storageApi.getStorageByType(StorageType.STAGING);

        var body = RequestBody.fromBytes(bytes);
        var key = key(storage.path(), UUID.randomUUID().toString());
        s3Client.putObject(builder -> builder.bucket(storage.bucket()).key(key), body);
        return new S3Path(storage.bucket(), key);
    }

    @Override
    public void moveToPermanentStorage(S3Path path) {
        var permanentStorage = storageApi.getStorageByType(StorageType.PERMANENT);

        s3Client.copyObject(builder -> builder
            .sourceBucket(path.bucket())
            .sourceKey(path.key())
            .destinationBucket(permanentStorage.bucket())
            .destinationKey(replaceFolder(path.key(), permanentStorage.path())));

        s3Client.deleteObject(builder -> builder
            .bucket(path.bucket())
            .key(path.key()));
    }

    private String key(String... parts) {
        StringJoiner joiner = new StringJoiner("/");
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            for (String segment : part.replace('\\', '/').split("/")) {
                if (segment.isBlank()) {
                    continue;
                }
                if (segment.equals(".") || segment.equals("..")) {
                    throw new FileStorageException("Invalid path segment: " + segment);
                }
                joiner.add(segment.trim());
            }
        }
        return joiner.toString();
    }

    public String replaceFolder(String existingKey, String newFolder) {
        String fileName = fileName(existingKey);
        if (fileName.isEmpty()) {
            throw new FileStorageException("Key has no file name: " + existingKey);
        }
        return key(newFolder, fileName);
    }

    public String fileName(String key) {
        String normalized = key(key);
        int i = normalized.lastIndexOf('/');
        return i < 0 ? normalized : normalized.substring(i + 1);
    }

    public String folder(String key) {
        String normalized = key(key);
        int i = normalized.lastIndexOf('/');
        return i < 0 ? "" : normalized.substring(0, i);
    }

    @SneakyThrows
    @Override
    @Retryable(includes = S3Exception.class, multiplier = 2)
    public byte[] getByPath(S3Path path) {
        return s3Client.getObject(builder -> builder.bucket(path.bucket()).key(path.key().toString()))
            .readAllBytes();
    }

    @Override
    @Retryable(includes = S3Exception.class, multiplier = 2)
    public void deleteAll(List<S3Path> paths) {
        var identifiersByBucket = paths.stream()
            .collect(groupingBy(S3Path::bucket, mapping(this::toObjectIdentifier, toList())));


        identifiersByBucket.forEach((bucket, keys) -> {

            var response = s3Client.deleteObjects(builder -> builder.bucket(bucket).delete(db -> db.objects(keys)));
            if (response.hasErrors()) {
                log.error("Failed to delete some objects from bucket {}: {}", bucket, response.errors());
                throw new FileStorageDeleteException(bucket, response.errors().getFirst().key());
            }
        });
    }

    private ObjectIdentifier toObjectIdentifier(S3Path path) {
        return ObjectIdentifier.builder()
            .key(path.key().toString())
            .build();
    }
}
