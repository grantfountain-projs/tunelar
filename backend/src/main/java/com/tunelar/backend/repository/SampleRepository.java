package com.tunelar.backend.repository;

import com.tunelar.backend.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    List<Sample> findByUserId(Long userId);
    List<Sample> findByTitleContainingIgnoreCase(String title);
}