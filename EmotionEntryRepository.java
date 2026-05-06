package com.mentalhealth.repository;

import com.mentalhealth.model.UserEmotionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionEntryRepository extends JpaRepository<UserEmotionEntry, Long> {
    List<UserEmotionEntry> findTop5ByOrderByIdDesc();
}
