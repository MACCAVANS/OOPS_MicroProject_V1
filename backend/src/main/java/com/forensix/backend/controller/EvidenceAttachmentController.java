package com.forensix.backend.controller;

import com.forensix.backend.entity.EvidenceAttachment;
import com.forensix.backend.service.EvidenceAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EvidenceAttachmentController {

    @Autowired
    private EvidenceAttachmentService attachmentService;

    /** Upload one file attached to an evidence record */
    @PostMapping("/api/evidence/{evidenceId}/attachments")
    public ResponseEntity<?> upload(
            @PathVariable String evidenceId,
            @RequestParam("file") MultipartFile file) {
        try {
            EvidenceAttachment saved = attachmentService.saveAttachment(evidenceId, file);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    /** List metadata for all attachments of an evidence record */
    @GetMapping("/api/evidence/{evidenceId}/attachments")
    public ResponseEntity<List<EvidenceAttachment>> list(@PathVariable String evidenceId) {
        return ResponseEntity.ok(attachmentService.getAttachments(evidenceId));
    }

    /** Serve / download a single attachment file */
    @GetMapping("/api/evidence/attachments/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        try {
            EvidenceAttachment attachment = attachmentService.getAttachment(id);
            byte[] data = attachmentService.readFileBytes(id);

            String ct = (attachment.getContentType() != null && !attachment.getContentType().isBlank())
                    ? attachment.getContentType()
                    : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(ct));
            // Use inline so the browser can preview images, PDFs, videos
            headers.setContentDisposition(
                    ContentDisposition.inline()
                            .filename(attachment.getOriginalFilename())
                            .build());
            headers.setContentLength(data.length);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** Delete a single attachment */
    @DeleteMapping("/api/evidence/attachments/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            attachmentService.deleteAttachment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
