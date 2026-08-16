package com.epam.storage.mapper;

import com.epam.storage.dto.CreateStorageDto;
import com.epam.storage.dto.StorageDto;
import com.epam.storage.entity.StorageEntity;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StorageMapper {

    StorageEntity toEntity(CreateStorageDto dto);

    StorageDto toDto(StorageEntity entity);
}
