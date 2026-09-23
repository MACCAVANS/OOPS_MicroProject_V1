package com.forensix.backend.service;

import com.forensix.backend.entity.InvestigationCase;
import com.forensix.backend.repository.CaseRepository;
import com.forensix.backend.repository.EvidenceRepository;
import com.forensix.backend.repository.SuspectRepository;
import com.forensix.backend.entity.Evidence;
import com.forensix.backend.entity.Suspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {
    
    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private SuspectRepository suspectRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    public List<InvestigationCase> getAllCases() {
        return caseRepository.findAll();
    }

    public Optional<InvestigationCase> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    public InvestigationCase createCase(InvestigationCase newCase) {
        return caseRepository.save(newCase);
    }

    @Transactional
    public InvestigationCase updateCase(Long id, InvestigationCase updatedCase) {
        return caseRepository.findById(id).map(existingCase -> {
            String oldCaseNumber = existingCase.getCaseNumber();
            String newCaseNumber = updatedCase.getCaseNumber();

            existingCase.setCaseNumber(newCaseNumber);
            existingCase.setCaseTitle(updatedCase.getCaseTitle());
            existingCase.setLocation(updatedCase.getLocation());
            existingCase.setIncidentDate(updatedCase.getIncidentDate());
            existingCase.setStatus(updatedCase.getStatus());
            existingCase.setInvestigator(updatedCase.getInvestigator());
            existingCase.setDescription(updatedCase.getDescription());
            
            InvestigationCase savedCase = caseRepository.save(existingCase);

            if (oldCaseNumber != null && !oldCaseNumber.equals(newCaseNumber)) {
                List<Suspect> suspects = suspectRepository.findByAssociatedCase(oldCaseNumber);
                for (Suspect s : suspects) {
                    s.setAssociatedCase(newCaseNumber);
                    suspectRepository.save(s);
                }

                List<Evidence> evidenceList = evidenceRepository.findByCaseId(oldCaseNumber);
                for (Evidence e : evidenceList) {
                    e.setCaseId(newCaseNumber);
                    evidenceRepository.save(e);
                }
            }

            return savedCase;
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
