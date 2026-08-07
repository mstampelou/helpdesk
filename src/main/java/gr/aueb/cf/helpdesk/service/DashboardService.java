package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.AgentWorkloadDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getStats();
    List<AgentWorkloadDTO> getAgentWorkload();
    List<TicketReadOnlyDTO> getRecentTickets();
    List<TicketReadOnlyDTO> getOrphanedTickets();
    List<TicketReadOnlyDTO> getUnassignedTickets();
}