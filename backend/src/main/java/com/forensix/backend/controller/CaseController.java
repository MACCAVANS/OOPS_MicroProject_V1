package com.forensix.backend.controller;

import com.forensix.backend.entity.InvestigationCase;
import com.forensix.backend.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @GetMapping
    public List<InvestigationCase> getAllCases() {
        return caseService.getAllCases();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestigationCase> getCaseById(@PathVariable Long id) {
        return caseService.getCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InvestigationCase createCase(@RequestBody InvestigationCase newCase) {
        return caseService.createCase(newCase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestigationCase> updateCase(@PathVariable Long id, @RequestBody InvestigationCase updatedCase) {
        try {
            return ResponseEntity.ok(caseService.updateCase(id, updatedCase));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/conclusion")
    public ResponseEntity<InvestigationCase> updateConclusion(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        try {
            String conclusion = payload.get("conclusion");
            return ResponseEntity.ok(caseService.updateConclusion(id, conclusion));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
