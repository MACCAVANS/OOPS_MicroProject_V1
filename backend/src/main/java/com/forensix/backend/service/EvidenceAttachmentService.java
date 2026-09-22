package com.forensix.backend.service;

import com.forensix.backend.entity.EvidenceAttachment;
import com.forensix.backend.repository.EvidenceAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceAttachmentService {

    @Autowired
    private EvidenceAttachmentRepository attachmentRepository;

    @Value("${forensix.upload.dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024; // 50 MB

    public EvidenceAttachment saveAttachment(String evidenceId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File exceeds the 50 MB size limit");
        }

        // Sanitise original filename — strip any path components and unsafe chars
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid or missing filename");
        }
        String safeName = Paths.get(originalFilename).getFileName().toString()
                .replaceAll("[^a-zA-Z0-9._\\-]", "_");

        String storedFilename = UUID.randomUUID() + "_" + safeName;

        // Resolve absolute upload directory and prevent traversal
        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path evidenceDir = baseDir.resolve(evidenceId).normalize();
        if (!evidenceDir.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid evidence ID");
        }
        Files.createDirectories(evidenceDir);

        Path targetPath = evidenceDir.resolve(storedFilename).normalize();
        if (!targetPath.startsWith(evidenceDir)) {
            throw new IllegalArgumentException("Invalid filename after sanitisation");
        }

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        EvidenceAttachment attachment = new EvidenceAttachment();
        attachment.setEvidenceId(evidenceId);
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(storedFilename);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadTimestamp(LocalDateTime.now());
        attachment.setStoragePath(targetPath.toString());

        return attachmentRepository.save(attachment);
    }

    public List<EvidenceAttachment> getAttachments(String evidenceId) {
        return attachmentRepository.findByEvidenceId(evidenceId);
    }

    public EvidenceAttachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + id));
    }

    public byte[] readFileBytes(Long attachmentId) throws IOException {
        EvidenceAttachment attachment = getAttachment(attachmentId);
        Path filePath = Paths.get(attachment.getStoragePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Physical file not found for attachment " + attachmentId);
        }
        return Files.readAllBytes(filePath);
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        EvidenceAttachment attachment = getAttachment(attachmentId);
        Files.deleteIfExists(Paths.get(attachment.getStoragePath()));
        attachmentRepository.deleteById(attachmentId);
    }

    public void deleteAllForEvidence(String evidenceId) {
        List<EvidenceAttachment> attachments = attachmentRepository.findByEvidenceId(evidenceId);
        for (EvidenceAttachment a : attachments) {
            try {
                Files.deleteIfExists(Paths.get(a.getStoragePath()));
            } catch (IOException e) {
                // Log and continue — do not abort the delete of other files
                System.err.println("Warning: could not delete file " + a.getStoragePath() + ": " + e.getMessage());
            }
        }
        attachmentRepository.deleteByEvidenceId(evidenceId);
    }
}
