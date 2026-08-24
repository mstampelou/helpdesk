package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.dto.AttachmentDownloadDTO;
import gr.aueb.cf.helpdesk.dto.AttachmentUploadResultDTO;
import gr.aueb.cf.helpdesk.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/tickets/{uuid}/attachments")
    public String upload(@PathVariable String uuid,
                         @RequestParam("file") MultipartFile[] files,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        AttachmentUploadResultDTO result = attachmentService.uploadAttachments(uuid, files, authentication.getName());
        if (!result.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("attachmentErrors", result.getErrors());
        }
        return "redirect:/tickets/" + uuid;
    }

    @GetMapping("/attachments/{uuid}/download")
    public ResponseEntity<Resource> download(@PathVariable String uuid) {
        AttachmentDownloadDTO download = attachmentService.downloadAttachment(uuid);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.getFileName() + "\"")
                .body(download.getResource());
    }

    @PostMapping("/tickets/{ticketUuid}/attachments/{attachmentUuid}/delete")
    public String delete(@PathVariable String ticketUuid, @PathVariable String attachmentUuid) {
        attachmentService.deleteAttachment(ticketUuid, attachmentUuid);
        return "redirect:/tickets/" + ticketUuid;
    }
}