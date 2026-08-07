package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    @EntityGraph(attributePaths = {"category", "assignedTo", "tags"})
    Page<Ticket> findByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "assignedTo", "createdBy", "tags"})
    Optional<Ticket> findByUuidAndDeletedFalse(String uuid);

    long countByDeletedFalseAndStatus(TicketStatus status);
    long countByDeletedFalseAndPriority(TicketPriority priority);
    long countByDeletedFalseAndAssignedToAndStatusIn(User assignedTo, List<TicketStatus> statuses);
    long countByDeletedFalseAndCreatedBy(User createdBy);

    // Orphaned tickets: active (OPEN/IN_PROGRESS), still assigned to someone,
    // but that someone is no longer a SUPPORT agent (e.g. demoted to USER).
    List<Ticket> findByDeletedFalseAndStatusInAndAssignedTo_RoleNot(List<TicketStatus> statuses, Role role);

    List<Ticket> findByDeletedFalseAndStatusInAndAssignedToIsNull(List<TicketStatus> statuses);
}