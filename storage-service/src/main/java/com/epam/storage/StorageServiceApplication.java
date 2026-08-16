package com.epam.storage;

import com.epam.storage.dto.CreateStorageDto;
import com.epam.storage.dto.StorageType;
import com.epam.storage.serivce.StorageService;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@SpringBootApplication
public class StorageServiceApplication {

    static void main(String[] args) {
		SpringApplication.run(StorageServiceApplication.class, args);
	}

    @Bean
    public CommandLineRunner commandLineRunner(StorageService storageService) {
        return args -> {
            storageService.createStorage(new CreateStorageDto(StorageType.PERMANENT, "storage", "permanent/"));
            storageService.createStorage(new CreateStorageDto(StorageType.STAGING, "storage", "staging/"));
        };
    }
}
