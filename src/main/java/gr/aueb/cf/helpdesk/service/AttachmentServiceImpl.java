package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.AttachmentDownloadDTO;
import gr.aueb.cf.helpdesk.dto.AttachmentReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.AttachmentUploadResultDTO;
import gr.aueb.cf.helpdesk.exception.AttachmentNotFoundException;
import gr.aueb.cf.helpdesk.exception.TicketNotFoundException;
import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.Attachment;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.repository.AttachmentRepository;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "application/pdf", "text/plain"
    );

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AttachmentReadOnlyDTO uploadAttachment(String ticketUuid, MultipartFile file, String currentUsername) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file was uploaded.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File is too large — max allowed size is 5MB.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed: images, PDF, plain text.");
        }

        Ticket ticket = ticketRepository.findByUuidAndDeletedFalse(ticketUuid)
                .orElseThrow(() -> new TicketNotFoundException(ticketUuid));
        User uploader = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(currentUsername));


        String originalFileName = Paths.get(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        ).getFileName().toString();
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path target = uploadPath.resolve(storedFileName).normalize();

            if (!target.startsWith(uploadPath)) {
                throw new IllegalArgumentException("Invalid file name.");
            }

            file.transferTo(target);

            Attachment attachment = new Attachment();
            attachment.setTicket(ticket);
            attachment.setFileName(originalFileName);
            attachment.setFilePath(target.toString());
            attachment.setContentType(contentType);
            attachment.setFileSize(file.getSize());
            attachment.setUploadedBy(uploader);

            Attachment saved = attachmentRepository.save(attachment);
            log.info("Attachment uploaded: ticket={}, file='{}', size={} bytes, by={}", ticketUuid, originalFileName, file.getSize(), currentUsername);
            return toReadOnlyDTO(saved);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not store the uploaded file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentReadOnlyDTO> getAttachmentsForTicket(String ticketUuid) {
        Ticket ticket = ticketRepository.findByUuidAndDeletedFalse(ticketUuid)
                .orElseThrow(() -> new TicketNotFoundException(ticketUuid));
        return attachmentRepository.findByTicketAndDeletedFalseOrderByCreatedAtAsc(ticket).stream()
                .map(this::toReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDownloadDTO downloadAttachment(String attachmentUuid) {
        Attachment attachment = attachmentRepository.findByUuidAndDeletedFalse(attachmentUuid)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentUuid));
        try {
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new AttachmentNotFoundException(attachmentUuid);
            }
            return new AttachmentDownloadDTO(resource, attachment.getFileName(), attachment.getContentType());
        } catch (MalformedURLException e) {
            throw new AttachmentNotFoundException(attachmentUuid);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    @Override
    @Transactional
    public void deleteAttachment(String ticketUuid, String attachmentUuid) {
        Attachment attachment = attachmentRepository.findByUuidAndDeletedFalse(attachmentUuid)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentUuid));
        attachment.setDeleted(true); // soft delete — the file itself stays on disk untouched
        log.info("Attachment soft-deleted: uuid={}", attachmentUuid);
    }

    @Override
    @Transactional
    public AttachmentUploadResultDTO uploadAttachments(String ticketUuid, MultipartFile[] files, String currentUsername) {
        List<AttachmentReadOnlyDTO> uploaded = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            try {
                uploaded.add(uploadAttachment(ticketUuid, file, currentUsername));
            } catch (IllegalArgumentException e) {
                String name = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
                errors.add(name + ": " + e.getMessage());
            }
        }
        return new AttachmentUploadResultDTO(uploaded, errors);
    }

    private AttachmentReadOnlyDTO toReadOnlyDTO(Attachment a) {
        return new AttachmentReadOnlyDTO(
                a.getUuid(),
                a.getFileName(),
                a.getContentType(),
                a.getFileSize(),
                a.getUploadedBy().getFullName(),
                a.getCreatedAt()
        );
    }
}