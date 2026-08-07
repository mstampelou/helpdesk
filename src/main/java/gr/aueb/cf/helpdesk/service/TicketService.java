package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.TicketDetailDTO;
import gr.aueb.cf.helpdesk.dto.TicketInsertDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TicketUpdateDTO;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {
    TicketReadOnlyDTO insertTicket(TicketInsertDTO dto, String currentUsername);
    Page<TicketReadOnlyDTO> findPaginated(String search, TicketStatus status, TicketPriority priority,
                                          Pageable pageable, String currentUsername);
    TicketDetailDTO findByUuid(String uuid);
    void addComment(String ticketUuid, String body, boolean internalNote, String currentUsername);

    TicketUpdateDTO getForEdit(String uuid);
    void updateTicket(String uuid, TicketUpdateDTO dto, String currentUsername);
    void deleteTicket(String uuid);

    void assignTicket(String uuid, String agentUuid, String reason, String currentUsername);
}
