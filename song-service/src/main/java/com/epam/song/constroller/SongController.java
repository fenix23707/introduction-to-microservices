package com.epam.song.constroller;

import com.epam.common.dto.song.CreateSongMetadataResponse;
import com.epam.common.dto.song.DeleteSongsMetadataResponse;
import com.epam.common.dto.song.SongMetadataDto;
import com.epam.song.service.SongService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @PostMapping
    public ResponseEntity<CreateSongMetadataResponse> createSongMetadata(@RequestBody SongMetadataDto songMetadataRequest) {
        return ResponseEntity.ok(songService.createSongMetadata(songMetadataRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongMetadataDto> getSongMetadata(@PathVariable String id) {
        return ResponseEntity.ok(songService.getSongMetadata(id));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongsMetadataResponse> deleteSongsMetadata(@RequestParam("id") String ids) {
        return ResponseEntity.ok(songService.deleteSongsMetadata(ids));
    }
}
