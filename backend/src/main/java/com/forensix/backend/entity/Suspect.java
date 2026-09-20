package com.forensix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "suspects")
public class Suspect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String suspectId;

    @Column(nullable = false)
    private String fullName;

    // We store the caseNumber from the frontend here, or map directly to InvestigationCase.
    // For simplicity, we can store the case number directly since frontend passes case ID strings like C-001
    private String associatedCase;

    private String location;
    private String status;
    private String contactInfo;

    @Column(length = 2000)
    private String notes;

    public Suspect() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSuspectId() { return suspectId; }
    public void setSuspectId(String suspectId) { this.suspectId = suspectId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAssociatedCase() { return associatedCase; }
    public void setAssociatedCase(String associatedCase) { this.associatedCase = associatedCase; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
