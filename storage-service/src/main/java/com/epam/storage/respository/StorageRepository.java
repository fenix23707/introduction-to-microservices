package com.epam.storage.respository;

import java.util.Optional;

import com.epam.storage.dto.StorageType;
import com.epam.storage.entity.StorageEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {

    Optional<StorageEntity> findFirstByStorageTypeOrderByCreatedAtDesc(StorageType storageType);
}
