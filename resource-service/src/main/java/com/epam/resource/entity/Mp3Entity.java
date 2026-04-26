package com.epam.resource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Table(name = "mp3_files")
@Accessors(chain = true)
public class Mp3Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    @Column(nullable = false)
    private byte[] fileData;

}
