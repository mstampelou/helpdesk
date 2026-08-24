package gr.aueb.cf.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentReadOnlyDTO {
    private String uuid;
    private String fileName;
    private String contentType;
    private long fileSize;
    private String uploadedByName;
    private Instant createdAt;
}