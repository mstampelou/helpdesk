package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.AttachmentDownloadDTO;
import gr.aueb.cf.helpdesk.dto.AttachmentReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.AttachmentUploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    AttachmentReadOnlyDTO uploadAttachment(String ticketUuid, MultipartFile file, String currentUsername);
    List<AttachmentReadOnlyDTO> getAttachmentsForTicket(String ticketUuid);
    AttachmentDownloadDTO downloadAttachment(String attachmentUuid);
    void deleteAttachment(String ticketUuid, String attachmentUuid);
    AttachmentUploadResultDTO uploadAttachments(String ticketUuid, MultipartFile[] files, String currentUsername);
}