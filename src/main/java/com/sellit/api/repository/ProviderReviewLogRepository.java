package com.sellit.api.repository;

import com.sellit.api.Entity.ProviderReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderReviewLogRepository extends JpaRepository<ProviderReviewLog, Long> {
    Optional<ProviderReviewLog> findByUuid(String proverReviewLogUuid);
}
