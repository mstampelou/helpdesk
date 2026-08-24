package gr.aueb.cf.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentUploadResultDTO {
    private List<AttachmentReadOnlyDTO> uploaded;
    private List<String> errors;
}