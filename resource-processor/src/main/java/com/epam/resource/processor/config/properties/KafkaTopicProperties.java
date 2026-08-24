package com.epam.resource.processor.config.properties;

import com.google.common.base.Verify;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class KafkaTopicProperties {

    String name;

    public KafkaTopicProperties(String name) {
        Verify.verify(StringUtils.isNotBlank(name), "Topic name must not be blank");

        this.name = name;
    }
}
