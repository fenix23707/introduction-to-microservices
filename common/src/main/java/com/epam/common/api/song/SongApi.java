package com.epam.common.api.song;

import com.epam.common.dto.song.CreateSongMetadataResponse;
import com.epam.common.dto.song.DeleteSongsMetadataResponse;
import com.epam.common.dto.song.SongMetadataDto;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("${spring.http.client.service.group.song.base-url}/songs")
public interface SongApi {

    @PostExchange
    CreateSongMetadataResponse createSongMetadata(@RequestBody SongMetadataDto songMetadataRequest);

    @GetExchange("/{id}")
    SongMetadataDto getSongMetadata(@PathVariable String id);

    @DeleteExchange
    DeleteSongsMetadataResponse deleteSongsMetadata(@RequestParam("id") String ids);
}
