package com.epam.song.repository;

import java.util.List;

import com.epam.song.entity.SongEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Long> {


    @Modifying
    @Query(value = "DELETE FROM songs WHERE id IN :ids RETURNING id", nativeQuery = true)
    List<Long> deleteByIdIn(@Param("ids") List<Long> ids);
}
