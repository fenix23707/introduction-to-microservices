package com.epam.resource.processor.config;

import java.time.Duration;

import com.epam.common.api.resource.ResourceApi;
import com.epam.common.api.song.SongApi;

import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "song", types = SongApi.class)
@ImportHttpServices(group = "resource", types = ResourceApi.class)
@EnableDiscoveryClient
@EnableResilientMethods
@ConfigurationPropertiesScan
public class AppConfig {


    @Bean
    public RestClientCustomizer bearerTokenRelayCustomizer() {
        return builder -> builder.requestInterceptor((request, body, execution) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
            }
            return execution.execute(request, body);
        });
    }

    @Bean
    public Mp3Parser mp3Parser() {
        return new Mp3Parser();
    }

    @Bean
    public RetryTemplate httpRetryTemplate() {
        var retryPolicy = RetryPolicy.builder()
            .maxRetries(3)
            .delay(Duration.ofSeconds(1))
            .multiplier(2)
            .excludes(HttpClientErrorException.class)
            .build();
        return new RetryTemplate(retryPolicy);
    }
}
