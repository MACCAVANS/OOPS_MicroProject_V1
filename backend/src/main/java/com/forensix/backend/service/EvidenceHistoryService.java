package com.forensix.backend.service;

import com.forensix.backend.entity.EvidenceHistory;
import com.forensix.backend.repository.EvidenceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvidenceHistoryService {

    @Autowired
    private EvidenceHistoryRepository evidenceHistoryRepository;

    public List<EvidenceHistory> getAllHistory() {
        return evidenceHistoryRepository.findAllByOrderByTimestampDesc();
    }

    public List<EvidenceHistory> getHistoryByEvidenceId(String evidenceId) {
        return evidenceHistoryRepository.findByEvidenceIdOrderByTimestampDesc(evidenceId);
    }

    public EvidenceHistory createHistory(EvidenceHistory history) {
        return evidenceHistoryRepository.save(history);
    }
}
