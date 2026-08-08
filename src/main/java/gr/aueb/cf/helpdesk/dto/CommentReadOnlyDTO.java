package gr.aueb.cf.helpdesk.dto;

import gr.aueb.cf.helpdesk.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentReadOnlyDTO {
    private String authorInitials;
    private String authorFullName;
    private Role authorRole;
    private Instant createdAt;
    private boolean internalNote;
    private String body;
}
