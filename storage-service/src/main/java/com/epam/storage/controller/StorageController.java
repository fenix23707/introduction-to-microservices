package com.epam.storage.controller;

import java.util.List;

import com.epam.storage.dto.CreateStorageDto;
import com.epam.storage.dto.CreateStorageResponseDto;
import com.epam.storage.dto.StorageDto;
import com.epam.storage.dto.StorageType;
import com.epam.storage.serivce.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    @PostMapping
    public CreateStorageResponseDto createStorage(@RequestBody CreateStorageDto createStorageDto) {
        return storageService.createStorage(createStorageDto);
    }

    @GetMapping
    public List<StorageDto> getAll() {
        return storageService.getAll();
    }

    @GetMapping("/type/{type}")
    public StorageDto getStorageByType(@PathVariable("type") StorageType type) {
        return storageService.getStorageByType(type);
    }

    @DeleteMapping
    public void deleteStorages(@RequestParam("id") String csvIds) {
        storageService.deleteStorages(csvIds);
    }
}
