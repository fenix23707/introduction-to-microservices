package com.epam.common.api.storage;

import com.epam.common.dto.storage.StorageDto;
import com.epam.common.dto.storage.StorageType;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("${spring.http.client.service.group.storage.base-url}/storages")
public interface StorageApi {

    @GetExchange("/type/{type}")
    StorageDto getStorageByType(@PathVariable("type") StorageType type);
}
