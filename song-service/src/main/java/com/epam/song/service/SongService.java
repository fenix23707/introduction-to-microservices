package com.epam.song.service;

import com.epam.common.dto.song.CreateSongMetadataResponse;
import com.epam.common.dto.song.DeleteSongsMetadataResponse;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.common.service.IdsAsCsvParser;
import com.epam.song.exception.InvalidSongIdException;
import com.epam.song.exception.SongAlreadyExistsException;
import com.epam.song.exception.SongNotFoundException;
import com.epam.song.mapper.SongMapper;
import com.epam.song.repository.SongRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
public class SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final IdsAsCsvParser idsAsCsvParser;

    @Transactional
    public CreateSongMetadataResponse createSongMetadata(@Valid SongMetadataDto songMetadataRequest) {
        songRepository.findById(songMetadataRequest.id()).ifPresent(entity -> {
            throw new SongAlreadyExistsException(entity.getId());
        });

        var entity = songMapper.toEntity(songMetadataRequest);
        var savedEntity = songRepository.save(entity);
        return new CreateSongMetadataResponse(savedEntity.getId());
    }

    public SongMetadataDto getSongMetadata(String rawId) {
        var id = parseRawId(rawId);
        if (id <= 0) {
            throw new InvalidSongIdException(id);
        }

        return songRepository.findById(id)
            .map(songMapper::toDto)
            .orElseThrow(() -> new SongNotFoundException(id));
    }

    private static long parseRawId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new InvalidSongIdException(id);
        }
    }


    public DeleteSongsMetadataResponse deleteSongsMetadata(String ids) {
        var parsedIds = idsAsCsvParser.parseRawIdsString(ids);
        var deletedCount = songRepository.deleteByIdIn(parsedIds);
        return new DeleteSongsMetadataResponse(deletedCount);
    }
}
