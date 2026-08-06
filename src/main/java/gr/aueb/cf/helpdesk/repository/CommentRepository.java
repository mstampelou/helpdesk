package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.Comment;
import gr.aueb.cf.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByTicketAndDeletedFalseOrderByCreatedAtAsc(Ticket ticket);
}
