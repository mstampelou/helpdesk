package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.Attachment;
import gr.aueb.cf.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @EntityGraph(attributePaths = {"uploadedBy"})
    List<Attachment> findByTicketAndDeletedFalseOrderByCreatedAtAsc(Ticket ticket);

    Optional<Attachment> findByUuidAndDeletedFalse(String uuid);
}