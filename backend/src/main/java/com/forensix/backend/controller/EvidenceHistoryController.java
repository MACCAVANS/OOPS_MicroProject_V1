package com.forensix.backend.controller;

import com.forensix.backend.entity.EvidenceHistory;
import com.forensix.backend.service.EvidenceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class EvidenceHistoryController {

    @Autowired
    private EvidenceHistoryService evidenceHistoryService;

    @GetMapping
    public ResponseEntity<List<EvidenceHistory>> getAllHistory() {
        return ResponseEntity.ok(evidenceHistoryService.getAllHistory());
    }

    @GetMapping("/evidence/{evidenceId}")
    public ResponseEntity<List<EvidenceHistory>> getHistoryByEvidenceId(@PathVariable String evidenceId) {
        return ResponseEntity.ok(evidenceHistoryService.getHistoryByEvidenceId(evidenceId));
    }
}
