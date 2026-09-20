package com.forensix.backend.repository;

import com.forensix.backend.entity.EvidenceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvidenceHistoryRepository extends JpaRepository<EvidenceHistory, Long> {
    List<EvidenceHistory> findByEvidenceIdOrderByTimestampDesc(String evidenceId);
    List<EvidenceHistory> findAllByOrderByTimestampDesc();
}
