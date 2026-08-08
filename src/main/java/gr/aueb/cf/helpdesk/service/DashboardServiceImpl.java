package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.AgentWorkloadDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.model.Tag;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long open = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.OPEN);
        long inProgress = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.RESOLVED);
        long closed = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.CLOSED);

        stats.put("openCount", open);
        stats.put("inProgressCount", inProgress);
        stats.put("resolvedCount", resolved);
        stats.put("closedCount", closed);
        stats.put("totalTickets", open + inProgress + resolved + closed);

        stats.put("criticalCount", ticketRepository.countByDeletedFalseAndPriority(TicketPriority.CRITICAL));
        stats.put("highCount", ticketRepository.countByDeletedFalseAndPriority(TicketPriority.HIGH));
        stats.put("mediumCount", ticketRepository.countByDeletedFalseAndPriority(TicketPriority.MEDIUM));
        stats.put("lowCount", ticketRepository.countByDeletedFalseAndPriority(TicketPriority.LOW));
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentWorkloadDTO> getAgentWorkload() {
        List<User> agents = userRepository.findByRoleInAndDeletedFalseAndActiveTrue(List.of(Role.SUPPORT));
        List<TicketStatus> activeStatuses = List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS);

        return agents.stream()
                .map(a -> new AgentWorkloadDTO(
                        a.getInitials(),
                        a.getFullName(),
                        ticketRepository.countByDeletedFalseAndAssignedToAndStatusIn(a, activeStatuses)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketReadOnlyDTO> getRecentTickets() {
        return ticketRepository.findByDeletedFalse(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "updatedAt")))
                .stream()
                .map(this::toReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketReadOnlyDTO> getOrphanedTickets() {
        List<TicketStatus> activeStatuses = List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS);
        return ticketRepository.findOrphanedTickets(activeStatuses, Role.SUPPORT)
                .stream()
                .map(this::toReadOnlyDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketReadOnlyDTO> getUnassignedTickets() {
        List<TicketStatus> activeStatuses = List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS);
        return ticketRepository.findByDeletedFalseAndStatusInAndAssignedToIsNull(activeStatuses)
                .stream()
                .map(this::toReadOnlyDTO)
                .collect(Collectors.toList());
    }

    private TicketReadOnlyDTO toReadOnlyDTO(Ticket t) {
        return new TicketReadOnlyDTO(
                t.getUuid(),
                t.getTitle(),
                t.getStatus(),
                t.getPriority(),
                t.getAssignedTo() != null ? t.getAssignedTo().getFullName() : null,
                t.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                t.getUpdatedAt()
        );
    }
}
