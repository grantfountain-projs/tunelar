package com.tunelar.backend.repository;

import com.tunelar.backend.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> findByUserId(Long userId);
    List<Track> findByTitleContainingIgnoreCase(String title);
}