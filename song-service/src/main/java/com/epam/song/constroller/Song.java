package com.epam.song.constroller;

import com.epam.common.api.song.SongApi;
import com.epam.common.dto.song.CreateSongMetadataResponse;
import com.epam.common.dto.song.DeleteSongsMetadataResponse;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.song.service.SongService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/songs")
public class Song implements SongApi {

    private final SongService songService;

    @Override
    public CreateSongMetadataResponse createSongMetadata(@RequestBody SongMetadataDto songMetadataRequest) {
        return songService.createSongMetadata(songMetadataRequest);
    }

    @Override
    public SongMetadataDto getSongMetadata(@PathVariable String id) {
        return songService.getSongMetadata(id);
    }

    @Override
    public DeleteSongsMetadataResponse deleteSongsMetadata(@RequestParam("id") String ids) {
        return songService.deleteSongsMetadata(ids);
    }
}
