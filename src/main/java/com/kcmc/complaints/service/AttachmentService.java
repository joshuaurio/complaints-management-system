package com.kcmc.complaints.service;

import com.kcmc.complaints.model.Attachment;
import com.kcmc.complaints.model.Ticket;
import com.kcmc.complaints.model.User;
import com.kcmc.complaints.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    private final AttachmentRepository attRepo;
    private final LookupService lookup;

    @Value("${app.file-storage-path}")
    private String storagePath;

    public AttachmentService(AttachmentRepository attRepo, LookupService lookup) {
        this.attRepo = attRepo;
        this.lookup = lookup;
    }

    // 🔹 List attachments by ticket
    public List<Attachment> listByTicket(Long ticketId) {
        return attRepo.findByTicket_Id(ticketId);
    }

    // 🔹 Upload + Save attachment
    public Attachment create(Long ticketId, Long uploadedById, MultipartFile file) throws IOException {
        Ticket ticket = lookup.mustGetTicket(ticketId);
        User uploader = lookup.mustGetUser(uploadedById);

        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;
        long size = file.getSize();
        String mimeType = file.getContentType();

        Path base = Paths.get(storagePath);
        if (!Files.exists(base)) {
            Files.createDirectories(base);
        }
        Path dest = base.resolve(storedName);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        Attachment a = new Attachment();
        a.setTicket(ticket);
        a.setUploadedBy(uploader);
        a.setOriginalFileName(originalName);
        a.setStoredFileName(storedName);
        a.setFileSize(size);
        a.setMimeType(mimeType);

        return attRepo.save(a);
    }

    // 🔹 Delete attachment by ID
    public void delete(Long id) {
        Attachment att = getById(id);

        try {
            Path base = Paths.get(storagePath);
            Path file = base.resolve(att.getStoredFileName());
            Files.deleteIfExists(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from storage", e);
        }

        attRepo.delete(att);
    }

    // 🔹 Find attachment by ID
    public Attachment getById(Long id) {
        return attRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));
    }
}
