package gr.aueb.cf.helpdesk.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String uuid) {
        super("Ticket not found: " + uuid);
    }
}
