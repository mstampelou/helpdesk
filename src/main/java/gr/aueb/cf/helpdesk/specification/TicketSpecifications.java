package gr.aueb.cf.helpdesk.specification;

import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import org.springframework.data.jpa.domain.Specification;
import gr.aueb.cf.helpdesk.model.User;

public class TicketSpecifications {

    private TicketSpecifications() {}

    public static Specification<Ticket> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Ticket> hasStatus(TicketStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Ticket> hasPriority(TicketPriority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Ticket> titleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            return cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
        };
    }

    public static Specification<Ticket> createdBy(User user) {
        return (root, query, cb) -> user == null ? null : cb.equal(root.get("createdBy"), user);
    }
}
