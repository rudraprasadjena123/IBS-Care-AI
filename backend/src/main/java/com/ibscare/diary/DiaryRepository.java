package com.ibscare.diary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<DiaryEntry, String> {
    List<DiaryEntry> findTop500ByOrderByOccurredAtDesc();
}
