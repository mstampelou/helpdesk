package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    @EntityGraph(attributePaths = {"category", "assignedTo", "tags"})
    Page<Ticket> findByDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "assignedTo", "createdBy", "tags"})
    Optional<Ticket> findByUuidAndDeletedFalse(String uuid);

    long countByDeletedFalseAndStatus(TicketStatus status);
    long countByDeletedFalseAndPriority(TicketPriority priority);
    long countByDeletedFalseAndAssignedToAndStatusIn(gr.aueb.cf.helpdesk.model.User assignedTo, java.util.List<TicketStatus> statuses);
    long countByDeletedFalseAndCreatedBy(gr.aueb.cf.helpdesk.model.User createdBy);
}
