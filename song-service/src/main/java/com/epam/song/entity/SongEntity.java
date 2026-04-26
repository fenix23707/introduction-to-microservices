package com.epam.song.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "songs")
@Entity
public class SongEntity {

    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String artist;
    @Column(nullable = false)
    private String album;
    @Column(nullable = false, name = "duration_ms")
    private int durationMs;
    @Column(nullable = false)
    private Integer year;

}
