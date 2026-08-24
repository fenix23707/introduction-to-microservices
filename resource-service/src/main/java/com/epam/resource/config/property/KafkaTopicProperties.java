package com.epam.resource.config.property;

import com.google.common.base.Verify;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
public class KafkaTopicProperties {

    String name;

    public KafkaTopicProperties(String name) {
        Verify.verify(StringUtils.isNotBlank(name), "Topic name must not be blank");

        this.name = name;
    }
}
