package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void getOrphanedTickets_shouldReturnMappedResults() {
        Ticket ticket = new Ticket();
        ticket.setTitle("Orphaned ticket");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);

        when(ticketRepository.findOrphanedTickets(anyList(), any(Role.class))).thenReturn(List.of(ticket));

        List<TicketReadOnlyDTO> result = dashboardService.getOrphanedTickets();

        assertEquals(1, result.size());
        assertEquals("Orphaned ticket", result.get(0).getTitle());
    }

    @Test
    void getUnassignedTickets_shouldReturnMappedResults() {
        Ticket ticket = new Ticket();
        ticket.setTitle("Unassigned ticket");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.LOW);

        when(ticketRepository.findByDeletedFalseAndStatusInAndAssignedToIsNull(anyList()))
                .thenReturn(List.of(ticket));

        List<TicketReadOnlyDTO> result = dashboardService.getUnassignedTickets();

        assertEquals(1, result.size());
        assertEquals("Unassigned ticket", result.get(0).getTitle());
    }
}