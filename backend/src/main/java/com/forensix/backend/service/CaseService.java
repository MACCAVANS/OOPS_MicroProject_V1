package com.forensix.backend.service;

import com.forensix.backend.entity.InvestigationCase;
import com.forensix.backend.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CaseService {
    
    @Autowired
    private CaseRepository caseRepository;

    public List<InvestigationCase> getAllCases() {
        return caseRepository.findAll();
    }

    public Optional<InvestigationCase> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    public InvestigationCase createCase(InvestigationCase newCase) {
        return caseRepository.save(newCase);
    }

    public InvestigationCase updateCase(Long id, InvestigationCase updatedCase) {
        return caseRepository.findById(id).map(existingCase -> {
            existingCase.setCaseNumber(updatedCase.getCaseNumber());
            existingCase.setCaseTitle(updatedCase.getCaseTitle());
            existingCase.setLocation(updatedCase.getLocation());
            existingCase.setIncidentDate(updatedCase.getIncidentDate());
            existingCase.setStatus(updatedCase.getStatus());
            existingCase.setInvestigator(updatedCase.getInvestigator());
            existingCase.setDescription(updatedCase.getDescription());
            return caseRepository.save(existingCase);
        }).orElseThrow(() -> new RuntimeException("Case not found"));
    }

    public InvestigationCase updateConclusion(Long id, String conclusion) {
        return caseRepository.findById(id).map(c -> {
            c.setConclusion(conclusion);
            return caseRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Case not found"));
    }

    public void deleteCase(Long id) {
        caseRepository.deleteById(id);
    }
}
