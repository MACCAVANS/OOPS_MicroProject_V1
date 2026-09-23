package com.forensix.backend.service;

import com.forensix.backend.entity.Evidence;
import com.forensix.backend.entity.EvidenceHistory;
import com.forensix.backend.repository.EvidenceRepository;
import com.forensix.backend.repository.EvidenceHistoryRepository;
import com.forensix.backend.repository.EvidenceAttachmentRepository;
import com.forensix.backend.service.EvidenceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private EvidenceHistoryRepository evidenceHistoryRepository;

    @Autowired
    private EvidenceAttachmentRepository evidenceAttachmentRepository;

    @Autowired
    private EvidenceAttachmentService evidenceAttachmentService;

    public Evidence addEvidence(Evidence evidence) {
        if (evidenceRepository.existsByEvidenceId(evidence.getEvidenceId())) {
            throw new RuntimeException("Evidence ID already exists");
        }
        Evidence savedEvidence = evidenceRepository.save(evidence);
        
        // Record history
        recordHistory(savedEvidence.getEvidenceId(), "Evidence Collected", savedEvidence.getHandler(), savedEvidence.getStatus());
        
        return savedEvidence;
    }

    public List<Evidence> getAllEvidence() {
        return evidenceRepository.findAll();
    }

    public Optional<Evidence> getEvidenceById(Long id) {
        return evidenceRepository.findById(id);
    }

    public Evidence updateEvidenceStatus(Long id, String status, String handler) {
        Evidence evidence = evidenceRepository.findById(id).orElseThrow(() -> new RuntimeException("Evidence not found"));
        evidence.setStatus(status);
        if (handler != null && !handler.isEmpty()) {
            evidence.setHandler(handler);
        }
        Evidence updated = evidenceRepository.save(evidence);
        
        String action = "Status Updated";
        if ("Under Examination".equals(status)) action = "Examination Started";
        else if ("Examined".equals(status)) action = "Examination Completed";
        else if ("Stored".equals(status)) action = "Evidence Stored";

        recordHistory(updated.getEvidenceId(), action, updated.getHandler(), updated.getStatus());
        
        return updated;
    }

    @org.springframework.transaction.annotation.Transactional
    public Evidence updateEvidence(Long id, Evidence updatedEvidence) {
        return evidenceRepository.findById(id).map(existing -> {
            String oldEvidenceId = existing.getEvidenceId();
            String newEvidenceId = updatedEvidence.getEvidenceId();

            existing.setEvidenceId(newEvidenceId);
            existing.setType(updatedEvidence.getType());
            existing.setCaseId(updatedEvidence.getCaseId());
            existing.setCollectionDate(updatedEvidence.getCollectionDate());
            existing.setHandler(updatedEvidence.getHandler());
            existing.setStatus(updatedEvidence.getStatus());
            existing.setDescription(updatedEvidence.getDescription());
            existing.setNotes(updatedEvidence.getNotes());

            Evidence saved = evidenceRepository.save(existing);

            if (oldEvidenceId != null && !oldEvidenceId.equals(newEvidenceId)) {
                // Update History
                List<EvidenceHistory> history = evidenceHistoryRepository.findByEvidenceIdOrderByTimestampDesc(oldEvidenceId);
                for (EvidenceHistory h : history) {
                    h.setEvidenceId(newEvidenceId);
                    evidenceHistoryRepository.save(h);
                }
                // Update Attachments
                List<com.forensix.backend.entity.EvidenceAttachment> attachments = evidenceAttachmentRepository.findByEvidenceId(oldEvidenceId);
                for (com.forensix.backend.entity.EvidenceAttachment att : attachments) {
                    att.setEvidenceId(newEvidenceId);
                    evidenceAttachmentRepository.save(att);
                }
            }

            recordHistory(newEvidenceId, "Evidence Updated", existing.getHandler(), existing.getStatus());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Evidence not found"));
    }

    public void deleteEvidence(Long id) {
        Evidence evidence = evidenceRepository.findById(id).orElseThrow(() -> new RuntimeException("Evidence not found"));
        // Cascade-delete file attachments from disk + DB before removing the evidence record
        evidenceAttachmentService.deleteAllForEvidence(evidence.getEvidenceId());
        evidenceRepository.deleteById(id);
        recordHistory(evidence.getEvidenceId(), "Evidence Record Deleted", evidence.getHandler(), "Deleted");
    }

    private void recordHistory(String evidenceId, String action, String handler, String status) {
        EvidenceHistory history = new EvidenceHistory();
        history.setEvidenceId(evidenceId);
        history.setAction(action);
        history.setHandler(handler != null ? handler : "System");
        history.setTimestamp(LocalDateTime.now());
        history.setStatus(status);
        evidenceHistoryRepository.save(history);
    }
}
