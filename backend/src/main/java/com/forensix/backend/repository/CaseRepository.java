package com.forensix.backend.repository;

import com.forensix.backend.entity.InvestigationCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<InvestigationCase, Long> {
    Optional<InvestigationCase> findByCaseNumber(String caseNumber);
}
