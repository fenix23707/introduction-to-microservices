package com.epam.resource.service;

import java.io.InputStream;
import java.util.Collections;

import com.epam.common.api.song.SongApi;
import com.epam.common.service.IdsAsCsvParser;
import com.epam.resource.config.property.KafkaSongProperties;
import com.epam.resource.dto.Mp3DeleteResponse;
import com.epam.resource.dto.Mp3UploadResponse;
import com.epam.resource.dto.S3Path;
import com.epam.resource.dto.kafka.ResourceUploadEvent;
import com.epam.resource.entity.Mp3Entity;
import com.epam.resource.exception.InvalidResourceIdException;
import com.epam.resource.exception.Mp3FileParseException;
import com.epam.resource.exception.ResourceNotFoundException;
import com.epam.resource.repository.Mp3Repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tika.Tika;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class Mp3Service {

    @NonNull
    private final Mp3Repository mp3Repository;
    @NonNull
    private final IdsAsCsvParser idsAsCsvParser;
    @NonNull
    private final SongApi songApi;
    @NonNull
    private final Mp3FileStorage fileStorage;
    @NonNull
    private final KafkaTemplate<@NonNull String, @NonNull ResourceUploadEvent> kafkaTemplate;
    @NonNull
    private final KafkaSongProperties kafkaSongProperties;

    @SneakyThrows
    @Transactional
    public Mp3UploadResponse upload(@NonNull InputStream mp3File) {
        var bytes = mp3File.readAllBytes();

        verifyMp3File(bytes);

        var path = fileStorage.save(bytes);

        var entity = new Mp3Entity()
            .setBucket(path.bucket())
            .setObjectKey(path.key());
        mp3Repository.save(entity);

        kafkaTemplate.send(kafkaSongProperties.getName(), new ResourceUploadEvent(entity.getId()));

        return new Mp3UploadResponse(entity.getId());
    }

    public void verifyMp3File(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes)) {
            throw new Mp3FileParseException("Input stream is empty");
        }

        var tika = new Tika();
        var mimeType = tika.detect(bytes);
        if (!"audio/mpeg".equals(mimeType)) {
            throw new Mp3FileParseException("Invalid file type: " + mimeType + ". Expected audio/mpeg");
        }
    }

    @Transactional(readOnly = true)
    public byte[] download(String rawId) {
        var id = parseRawId(rawId);
        if (id <= 0) {
            throw new InvalidResourceIdException(id, "Must be a positive integer");
        }

        var path = mp3Repository.findById(id)
            .map(S3Path::fromEntity)
            .orElseThrow(() -> new ResourceNotFoundException(id));

        return fileStorage.getByPath(path);
    }

    private static long parseRawId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new InvalidResourceIdException(id, "Must be a positive integer");
        }
    }

    public Mp3DeleteResponse delete(String rawIdsAsCsvString) {
        var resourceIdsToDelete = idsAsCsvParser.parseRawIdsString(rawIdsAsCsvString);
        if (CollectionUtils.isEmpty(resourceIdsToDelete)) {
            return new Mp3DeleteResponse(Collections.emptyList());
        }

        var deletedEntities = mp3Repository.deleteAllByIdIn(resourceIdsToDelete);
        fileStorage.deleteAll(deletedEntities.stream().map(S3Path::fromEntity).toList());

        var deletedIds = deletedEntities.stream()
            .map(Mp3Entity::getId)
            .toList();
        if (CollectionUtils.isNotEmpty(deletedIds)) {
            var rawDeletedIdsAsCsvString = idsAsCsvParser.toRawIdsString(deletedIds);
            songApi.deleteSongsMetadata(rawDeletedIdsAsCsvString);
        }

        return new Mp3DeleteResponse(deletedIds);
    }


}
