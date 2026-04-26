package com.epam.resource.config;

import com.epam.common.api.song.SongApi;
import com.epam.common.config.CommonConfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.service.registry.ImportHttpServices;

@Slf4j
@Configuration
@Import(CommonConfig.class)
@ImportHttpServices(group = "song", types = SongApi.class)
public class AppConfig {

    @Bean
    public Mp3Parser mp3Parser() {
        return new Mp3Parser();
    }
}
