package gr.aueb.cf.helpdesk.dto;

import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailDTO {
    private String uuid;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private String categoryName;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;
    private List<CommentReadOnlyDTO> comments;
}
