package com.forensix.backend.controller;

import com.forensix.backend.service.NarrativeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private NarrativeReportService narrativeReportService;

    /**
     * POST /api/reports/{caseNumber}/narrative
     * Returns a factual narrative paragraph(s) for the specified case.
     * The AI key is never exposed to the frontend.
     */
    @PostMapping("/{caseNumber}/narrative")
    public ResponseEntity<?> generateNarrative(@PathVariable String caseNumber) {
        try {
            Map<String, String> result = narrativeReportService.generateNarrative(caseNumber);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
