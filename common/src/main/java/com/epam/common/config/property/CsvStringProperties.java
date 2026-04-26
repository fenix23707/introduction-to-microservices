package com.epam.common.config.property;

import static com.google.common.base.Verify.verify;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.csv-string")
public record CsvStringProperties(
    String delimiter,
    long maxLength
) {
    public CsvStringProperties {
        verify(StringUtils.isNoneBlank(delimiter), "Delimiter must not be blank");
        verify(maxLength > 0, "Max length must be positive");
    }
}
