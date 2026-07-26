package com.epam.resource.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.UUID;

import com.epam.resource.config.property.AwsS3Properties;
import com.epam.resource.dto.S3Path;
import com.epam.resource.exception.storage.FileStorageDeleteException;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
public class S3Mp3FileStorage implements Mp3FileStorage {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Mp3FileStorage(@NonNull S3Client s3Client,
                            @NonNull AwsS3Properties properties) {

        this.s3Client = s3Client;
        this.bucketName = properties.getBucketName();
    }

    @Override
    @Retryable(includes = S3Exception.class, multiplier = 2)
    public S3Path save(byte[] bytes) {
        var key = UUID.randomUUID();
        var body = RequestBody.fromBytes(bytes);
        var response = s3Client.putObject(builder -> builder.bucket(bucketName).key(key.toString()), body);
        log.info(response.toString());
        return new S3Path(bucketName, key);
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
