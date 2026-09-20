package com.forensix.backend.service;

import com.forensix.backend.entity.Suspect;
import com.forensix.backend.repository.SuspectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuspectService {

    @Autowired
    private SuspectRepository suspectRepository;

    public List<Suspect> getAllSuspects() {
        return suspectRepository.findAll();
    }

    public Optional<Suspect> getSuspectById(Long id) {
        return suspectRepository.findById(id);
    }

    public Suspect createSuspect(Suspect suspect) {
        return suspectRepository.save(suspect);
    }

    public Suspect updateSuspect(Long id, Suspect updatedSuspect) {
        return suspectRepository.findById(id).map(existing -> {
            existing.setSuspectId(updatedSuspect.getSuspectId());
            existing.setFullName(updatedSuspect.getFullName());
            existing.setAssociatedCase(updatedSuspect.getAssociatedCase());
            existing.setLocation(updatedSuspect.getLocation());
            existing.setStatus(updatedSuspect.getStatus());
            existing.setContactInfo(updatedSuspect.getContactInfo());
            existing.setNotes(updatedSuspect.getNotes());
            return suspectRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Suspect not found"));
    }

    public void deleteSuspect(Long id) {
        suspectRepository.deleteById(id);
    }
}
