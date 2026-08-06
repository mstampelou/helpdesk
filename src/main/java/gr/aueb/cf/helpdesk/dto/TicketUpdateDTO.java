package gr.aueb.cf.helpdesk.dto;

import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TicketUpdateDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private TicketPriority priority;

    @NotNull(message = "Category is required")
    private Long categoryId;

    // Only applied by the service when the caller is ADMIN/SUPPORT —
    // a plain USER editing their own ticket cannot change these two.
    private TicketStatus status;
    private String assignedToId; // this is the agent's UUID, not the raw PK

    private List<Long> tagIds;
}
