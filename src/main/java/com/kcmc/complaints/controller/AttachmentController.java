package com.kcmc.complaints.controller;

import com.kcmc.complaints.model.Attachment;
import com.kcmc.complaints.service.AttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@CrossOrigin
public class AttachmentController {

    private final AttachmentService svc;

    @Value("${app.file-storage-path}")
    private String storagePath;

    public AttachmentController(AttachmentService svc) {
        this.svc = svc;
    }

    // 🔹 List attachments for a ticket
    @GetMapping("/ticket/{ticketId}")
    public List<Attachment> byTicket(@PathVariable Long ticketId) {
        return svc.listByTicket(ticketId);
    }

    // 🔹 Upload attachment for a ticket
    @PostMapping("/ticket/{ticketId}")
    public ResponseEntity<Attachment> upload(@PathVariable Long ticketId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam Long uploadedById) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Attachment attachment = svc.create(ticketId, uploadedById, file);
        return ResponseEntity.ok(attachment);
    }

    // 🔹 Delete attachment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.ok().body(
                java.util.Map.of("message", "Attachment deleted successfully", "id", id)
        );
    }

    // 🔹 Download attachment by ID
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        Attachment att = svc.getById(id);

        Path filePath = Paths.get(storagePath).resolve(att.getStoredFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(att.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + att.getOriginalFileName() + "\"")
                .body(resource);
    }
}
