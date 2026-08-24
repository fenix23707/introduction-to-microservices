package com.epam.storage;

import com.epam.storage.dto.CreateStorageDto;
import com.epam.storage.dto.StorageType;
import com.epam.storage.serivce.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class StorageServiceApplication {

    static void main(String[] args) {
		SpringApplication.run(StorageServiceApplication.class, args);
	}

    @Bean
    public CommandLineRunner commandLineRunner(StorageService storageService) {
        return args -> {
            safeExecution(() -> storageService.createStorage(new CreateStorageDto(StorageType.PERMANENT, "storage", "permanent/")));
            safeExecution(() -> storageService.createStorage(new CreateStorageDto(StorageType.STAGING, "storage", "staging/")));
        };
    }

    private void safeExecution(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Error during execution: {}", e.getMessage(), e);
        }
    }
}
