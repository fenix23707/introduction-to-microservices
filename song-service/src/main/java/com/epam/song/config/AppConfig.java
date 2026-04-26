package com.epam.song.config;

import com.epam.common.config.CommonConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonConfig.class)
public class AppConfig {
}
