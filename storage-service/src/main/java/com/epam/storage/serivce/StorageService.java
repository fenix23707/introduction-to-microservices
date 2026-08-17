package com.epam.storage.serivce;

import java.util.List;

import com.epam.storage.dto.CreateStorageDto;
import com.epam.storage.dto.CreateStorageResponseDto;
import com.epam.storage.dto.StorageDto;
import com.epam.storage.dto.StorageType;
import com.epam.storage.exception.StorageAlreadyExistsException;
import com.epam.storage.exception.StorageNotFound;
import com.epam.storage.mapper.StorageMapper;
import com.epam.storage.respository.StorageRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;
    private final IdsAsCsvParser idsParser;
    private final S3Service s3Service;

    @Transactional
    public CreateStorageResponseDto createStorage(CreateStorageDto createStorageDto) {
        if (storageRepository.existsByBucketAndPath(createStorageDto.bucket(), createStorageDto.path())) {
            throw new StorageAlreadyExistsException(createStorageDto.bucket(), createStorageDto.path());
        }

        var entity = storageMapper.toEntity(createStorageDto);
        storageRepository.save(entity);
        s3Service.createBucket(createStorageDto.bucket());
        return new CreateStorageResponseDto(entity.getId());
    }

    @Transactional(readOnly = true)
    public List<StorageDto> getAll() {
        return storageRepository.findAll().stream()
                .map(storageMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StorageDto getStorageByType(StorageType type) {
        return storageRepository.findFirstByStorageTypeOrderByCreatedAtDesc(type)
                .map(storageMapper::toDto)
                .orElseThrow(() -> StorageNotFound.byType(type));
    }

    @Transactional
    public void deleteStorages(String csvIds) {
        var ids = idsParser.parseRawIdsString(csvIds);
        storageRepository.deleteAllById(ids);
    }
}
