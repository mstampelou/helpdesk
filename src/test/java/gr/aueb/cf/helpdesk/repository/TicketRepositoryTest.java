package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.Category;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findOrphanedTickets_shouldReturnOnlyRoleMismatchOrInactiveAgentTickets() {
        Category category = new Category();
        category.setName("Test Category A");
        category.setActive(true);
        entityManager.persistAndFlush(category);

        User activeSupportAgent = newUser("qa.active.agent", Role.SUPPORT, true);
        User inactiveSupportAgent = newUser("qa.inactive.agent", Role.SUPPORT, false);
        User demotedUser = newUser("qa.ex.support", Role.USER, true);
        User creator = newUser("qa.creator", Role.USER, true);
        entityManager.persistAndFlush(activeSupportAgent);
        entityManager.persistAndFlush(inactiveSupportAgent);
        entityManager.persistAndFlush(demotedUser);
        entityManager.persistAndFlush(creator);

        Ticket normalTicket = newTicket("Normal", category, creator, activeSupportAgent, TicketStatus.OPEN);
        Ticket inactiveAgentTicket = newTicket("Stuck with inactive agent", category, creator, inactiveSupportAgent, TicketStatus.OPEN);
        Ticket demotedAgentTicket = newTicket("Stuck with demoted agent", category, creator, demotedUser, TicketStatus.IN_PROGRESS);
        Ticket closedTicket = newTicket("Closed, ignore", category, creator, inactiveSupportAgent, TicketStatus.CLOSED);
        entityManager.persistAndFlush(normalTicket);
        entityManager.persistAndFlush(inactiveAgentTicket);
        entityManager.persistAndFlush(demotedAgentTicket);
        entityManager.persistAndFlush(closedTicket);

        List<Ticket> result = ticketRepository.findOrphanedTickets(
                List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS), Role.SUPPORT);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getTitle().equals("Stuck with inactive agent")));
        assertTrue(result.stream().anyMatch(t -> t.getTitle().equals("Stuck with demoted agent")));
    }

    @Test
    void findByDeletedFalseAndStatusInAndAssignedToIsNull_shouldReturnOnlyUnassignedActiveTickets() {
        Category category = new Category();
        category.setName("Test Category B");
        category.setActive(true);
        entityManager.persistAndFlush(category);

        User creator = newUser("qa.creator2", Role.USER, true);
        User agent = newUser("qa.agent2", Role.SUPPORT, true);
        entityManager.persistAndFlush(creator);
        entityManager.persistAndFlush(agent);

        Ticket unassigned = newTicket("Unassigned", category, creator, null, TicketStatus.OPEN);
        Ticket assigned = newTicket("Assigned", category, creator, agent, TicketStatus.OPEN);
        Ticket unassignedClosed = newTicket("Unassigned but closed", category, creator, null, TicketStatus.CLOSED);
        entityManager.persistAndFlush(unassigned);
        entityManager.persistAndFlush(assigned);
        entityManager.persistAndFlush(unassignedClosed);

        List<Ticket> result = ticketRepository.findByDeletedFalseAndStatusInAndAssignedToIsNull(
                List.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS));

        assertTrue(result.stream().anyMatch(t -> t.getTitle().equals("Unassigned")));
        assertTrue(result.stream().noneMatch(t -> t.getTitle().equals("Assigned")));
        assertTrue(result.stream().noneMatch(t -> t.getTitle().equals("Unassigned but closed")));
    }

    private User newUser(String username, Role role, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@corp.internal");
        user.setPasswordHash("hash");
        user.setFullName(username);
        user.setRole(role);
        user.setActive(active);
        return user;
    }

    private Ticket newTicket(String title, Category category, User creator, User assignedTo, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription("desc");
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setCategory(category);
        ticket.setCreatedBy(creator);
        ticket.setAssignedTo(assignedTo);
        ticket.setStatus(status);
        return ticket;
    }
}