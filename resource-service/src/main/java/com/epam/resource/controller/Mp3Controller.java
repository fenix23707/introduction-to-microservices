package com.epam.resource.controller;

import java.io.InputStream;

import com.epam.resource.dto.Mp3DeleteResponse;
import com.epam.resource.dto.Mp3UploadResponse;
import com.epam.resource.service.Mp3Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/resources")
public class Mp3Controller {

    private final Mp3Service mp3Service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = {"audio/mpeg", "audio/mpeg;charset=UTF-8"})
    public ResponseEntity<Mp3UploadResponse> uploadMp3(InputStream mp3File) {
        return ResponseEntity.ok(mp3Service.upload(mp3File));
    }

    @GetMapping(value = "/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> downloadMp3(@PathVariable String id) {
        return ResponseEntity.ok(mp3Service.download(id));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mp3DeleteResponse> deleteMp3(@RequestParam String id) {
        return ResponseEntity.ok(mp3Service.delete(id));
    }
}
