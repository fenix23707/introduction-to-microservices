package com.epam.resource.entity;

import java.util.UUID;

import com.epam.resource.dto.S3Path;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Table(
    name = "mp3_files",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_mp3_files_bucket_object_key",
        columnNames = {"bucket", "object_key"}
    )
)
@Accessors(chain = true)
public class Mp3Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private UUID objectKey;
}
