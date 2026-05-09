package com.epam.resource.config;

import com.epam.common.api.song.SongApi;
import com.epam.common.config.CommonConfig;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@Import(CommonConfig.class)
@ImportHttpServices(group = "song", types = SongApi.class)
@EnableDiscoveryClient
public class AppConfig {

    @Bean
    public Mp3Parser mp3Parser() {
        return new Mp3Parser();
    }
}
