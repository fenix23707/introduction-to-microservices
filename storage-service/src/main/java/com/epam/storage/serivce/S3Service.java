package com.epam.storage.serivce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3Service {

    private final S3Client s3Client;

    public void createBucket(String bucket) {
        try {
            s3Client.createBucket(it -> it.bucket(bucket));
            log.info("Bucket '{}' created", bucket);
        } catch (BucketAlreadyOwnedByYouException e) {
            log.debug("Bucket '{}' already exists", bucket);
        } catch (S3Exception e) {
            throw new IllegalStateException("Failed to create bucket '%s'".formatted(bucket), e);
        }
    }
}
