package com.forensix.backend.repository;

import com.forensix.backend.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    boolean existsByEvidenceId(String evidenceId);
}
