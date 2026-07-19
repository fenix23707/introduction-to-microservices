package com.epam.common.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("${spring.http.client.service.group.resource.base-url}/resources")
public interface ResourceApi {
    @GetExchange(value = "/{id}", accept = "audio/mpeg")
    ResponseEntity<byte[]> downloadMp3(@PathVariable String id);
}
