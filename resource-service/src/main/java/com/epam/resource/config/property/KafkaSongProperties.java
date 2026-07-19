package com.epam.resource.config.property;

import com.google.common.base.Verify;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "application.kafka.topic.song")
public class KafkaSongProperties {

    String name;

    public KafkaSongProperties(String name) {
        Verify.verify(StringUtils.isNotBlank(name), "Topic name must not be blank");

        this.name = name;
    }
}
