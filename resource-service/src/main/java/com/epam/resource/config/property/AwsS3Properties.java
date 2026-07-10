package com.epam.resource.config.property;

import static com.google.common.base.Verify.verify;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

import java.net.URI;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

@Getter
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties {

    private final String accessKey;
    private final String secretKey;
    private final URI endpoint;
    private final Region region;
    private final String bucketName;

    public AwsS3Properties(String accessKey,
                           String secretKey,
                           String endpoint,
                           String region,
                           String bucketName) {
        verify(isNoneBlank(accessKey), "Access key must not be blank");
        verify(isNoneBlank(secretKey), "Secret key must not be blank");
        verify(isNoneBlank(endpoint), "Endpoint must not be blank");
        verify(isNoneBlank(region), "Region must not be blank");
        verify(isNoneBlank(bucketName), "Bucket name must not be blank");

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = URI.create(endpoint);
        this.region = Region.of(region);
        this.bucketName = bucketName;
    }
}
