package com.forensix.backend.controller;

import com.forensix.backend.entity.Suspect;
import com.forensix.backend.service.SuspectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspects")
public class SuspectController {

    @Autowired
    private SuspectService suspectService;

    @GetMapping
    public List<Suspect> getAllSuspects() {
        return suspectService.getAllSuspects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Suspect> getSuspectById(@PathVariable Long id) {
        return suspectService.getSuspectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Suspect createSuspect(@RequestBody Suspect suspect) {
        return suspectService.createSuspect(suspect);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Suspect> updateSuspect(@PathVariable Long id, @RequestBody Suspect updatedSuspect) {
        try {
            return ResponseEntity.ok(suspectService.updateSuspect(id, updatedSuspect));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuspect(@PathVariable Long id) {
        suspectService.deleteSuspect(id);
        return ResponseEntity.noContent().build();
    }
}
