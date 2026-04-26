package com.epam.common.config;

import com.epam.common.controller.ExceptionControllerAdvice;
import com.epam.common.service.IdsAsCsvParser;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationPropertiesScan
@Import({IdsAsCsvParser.class, ExceptionControllerAdvice.class})
public class CommonConfig {
}
