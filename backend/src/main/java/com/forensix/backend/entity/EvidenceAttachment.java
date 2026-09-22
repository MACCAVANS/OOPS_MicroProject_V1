package com.forensix.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_attachments")
public class EvidenceAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links to Evidence.evidenceId (e.g. "E-001") */
    @Column(nullable = false)
    private String evidenceId;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    private String contentType;

    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadTimestamp;

    @Column(nullable = false, length = 1000)
    private String storagePath;

    public EvidenceAttachment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEvidenceId() { return evidenceId; }
    public void setEvidenceId(String evidenceId) { this.evidenceId = evidenceId; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadTimestamp() { return uploadTimestamp; }
    public void setUploadTimestamp(LocalDateTime uploadTimestamp) { this.uploadTimestamp = uploadTimestamp; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
}
