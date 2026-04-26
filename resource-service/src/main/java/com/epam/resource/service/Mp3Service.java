package com.epam.resource.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;

import com.epam.common.api.song.SongApi;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.common.jackson.MmSsDurationSerializer;
import com.epam.common.service.IdsAsCsvParser;
import com.epam.resource.dto.Mp3DeleteResponse;
import com.epam.resource.dto.Mp3UploadResponse;
import com.epam.resource.entity.Mp3Entity;
import com.epam.resource.exception.InvalidResourceIdException;
import com.epam.resource.exception.Mp3FileParseException;
import com.epam.resource.exception.ResourceNotFoundException;
import com.epam.resource.repository.Mp3Repository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
@Service
public class Mp3Service {

    private final Mp3Repository mp3Repository;
    private final IdsAsCsvParser idsAsCsvParser;
    private final SongApi songApi;
    private final Mp3Parser parser;

    @SneakyThrows
    @Transactional
    public Mp3UploadResponse upload(@NonNull InputStream mp3File) {
        var bytes = mp3File.readAllBytes();
        var entity = new Mp3Entity().setFileData(bytes);
        mp3Repository.save(entity);
        var fileId = entity.getId();

        var songMetadataDto = parseSongMetadata(fileId, bytes);
        songApi.createSongMetadata(songMetadataDto);

        return new Mp3UploadResponse(fileId);
    }

    private SongMetadataDto parseSongMetadata(Long fileId, byte[] bytes) throws IOException, SAXException {
        try {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();

            parser.parse(new ByteArrayInputStream(bytes), handler, metadata, pcontext);

            var duration = Optional.ofNullable(metadata.get("xmpDM:duration"))
                .map(Double::parseDouble)
                .map(d -> Duration.ofSeconds((long) d.doubleValue()))
                .map(MmSsDurationSerializer::toMmSsString)
                .orElse(null);
            return SongMetadataDto.builder()
                .id(fileId)
                .name(metadata.get("dc:title"))
                .album(metadata.get("xmpDM:album"))
                .artist(metadata.get("xmpDM:artist"))
                .duration(duration)
                .year(metadata.get("xmpDM:releaseDate"))
                .build();
        } catch (NumberFormatException | TikaException ex) {
            throw new Mp3FileParseException(ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] download(String rawId) {
        var id = parseRawId(rawId);
        if (id <= 0) {
            throw new InvalidResourceIdException(id, "Must be a positive integer");
        }

        return mp3Repository.findById(id)
            .map(Mp3Entity::getFileData)
            .orElseThrow(() -> new ResourceNotFoundException(id));
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
        var deletedIds = mp3Repository.deleteAllByIdIn(resourceIdsToDelete);

        songApi.deleteSongsMetadata(rawIdsAsCsvString);

        return new Mp3DeleteResponse(deletedIds);
    }


}
