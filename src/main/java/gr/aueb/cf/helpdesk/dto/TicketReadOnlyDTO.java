package gr.aueb.cf.helpdesk.dto;

import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketReadOnlyDTO {
    private String uuid;
    private String title;
    private TicketStatus status;
    private TicketPriority priority;
    private String assignedToName;
    private List<String> tags;
    private Instant updatedAt;
}
