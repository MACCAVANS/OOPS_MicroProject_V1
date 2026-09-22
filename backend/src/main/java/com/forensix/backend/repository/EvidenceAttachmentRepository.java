package com.forensix.backend.repository;

import com.forensix.backend.entity.EvidenceAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EvidenceAttachmentRepository extends JpaRepository<EvidenceAttachment, Long> {

    List<EvidenceAttachment> findByEvidenceId(String evidenceId);

    @Transactional
    void deleteByEvidenceId(String evidenceId);
}
