package com.epam.resource.repository;

import java.util.List;

import com.epam.resource.entity.Mp3Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Mp3Repository extends JpaRepository<Mp3Entity, Long> {

    @Modifying
    @Query(value = "DELETE FROM mp3_files WHERE id IN :ids RETURNING id", nativeQuery = true)
    List<Long> deleteAllByIdIn(@Param("ids") List<Long> ids);
}
