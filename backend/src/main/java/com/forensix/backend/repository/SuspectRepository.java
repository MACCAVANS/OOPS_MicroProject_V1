package com.forensix.backend.repository;

import com.forensix.backend.entity.Suspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspectRepository extends JpaRepository<Suspect, Long> {
    Optional<Suspect> findBySuspectId(String suspectId);
    List<Suspect> findByAssociatedCase(String associatedCase);
}
