package com.forensix.backend.service;

import com.forensix.backend.entity.*;
import com.forensix.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates report narrative generation.
 * Tries the AI service first; falls back to the rule-based generator if AI
 * is unavailable or throws any exception.
 */
@Service
public class NarrativeReportService {

    @Autowired private CaseRepository caseRepository;
    @Autowired private SuspectRepository suspectRepository;
    @Autowired private EvidenceRepository evidenceRepository;
    @Autowired private EvidenceHistoryRepository historyRepository;
    @Autowired private AIReportService aiReportService;
    @Autowired private RuleBasedNarrativeService ruleBasedNarrativeService;

    /**
     * Generates a narrative for the given case number.
     * Returns a map with keys:
     *   "narrative" — the generated text
     *   "source"    — "ai" | "rule-based"
     *   "note"      — human-readable source description
     */
    public Map<String, String> generateNarrative(String caseNumber) {

        InvestigationCase theCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new RuntimeException("Case not found: " + caseNumber));

        // Gather related data from the database
        List<Suspect> suspects = suspectRepository.findByAssociatedCase(caseNumber);
        List<Evidence> evidence = evidenceRepository.findByCaseId(caseNumber);

        // Collect all history for every evidence item belonging to this case
        Set<String> evidenceIds = evidence.stream()
                .map(Evidence::getEvidenceId)
                .collect(Collectors.toSet());
        List<EvidenceHistory> history = historyRepository.findAllByOrderByTimestampDesc()
                .stream()
                .filter(h -> evidenceIds.contains(h.getEvidenceId()))
                .collect(Collectors.toList());

        // Try AI first
        try {
            String narrative = aiReportService.generateNarrative(theCase, suspects, evidence, history);
            Map<String, String> result = new LinkedHashMap<>();
            result.put("narrative", narrative);
            result.put("source", "ai");
            result.put("note", "AI-assisted narrative generated from recorded case data.");
            return result;
        } catch (AIReportService.AIUnavailableException e) {
            // Expected when no key is configured — fall through to rule-based
        } catch (Exception e) {
            // Any other AI error — fall through to rule-based
            System.err.println("AI narrative failed, using rule-based fallback: " + e.getMessage());
        }

        // Rule-based fallback
        String narrative = ruleBasedNarrativeService.generateNarrative(theCase, suspects, evidence, history);
        Map<String, String> result = new LinkedHashMap<>();
        result.put("narrative", narrative);
        result.put("source", "rule-based");
        result.put("note", "Generated from recorded case data.");
        return result;
    }
}
