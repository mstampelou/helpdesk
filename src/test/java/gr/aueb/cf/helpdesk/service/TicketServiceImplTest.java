package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.TicketDetailDTO;
import gr.aueb.cf.helpdesk.dto.TicketInsertDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.exception.CategoryNotFoundException;
import gr.aueb.cf.helpdesk.dto.TicketUpdateDTO;
import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import gr.aueb.cf.helpdesk.model.Category;
import gr.aueb.cf.helpdesk.model.Comment;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void insertTicket_shouldSaveTicketWithCorrectCreator() {
        // Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("Hardware");

        User creator = new User();
        creator.setUsername("user.demo");
        creator.setFullName("Alex Doukas");

        TicketInsertDTO dto = new TicketInsertDTO();
        dto.setTitle("Laptop won't turn on");
        dto.setDescription("Pressed the power button, nothing happens.");
        dto.setPriority(TicketPriority.HIGH);
        dto.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findByUsername("user.demo")).thenReturn(Optional.of(creator));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TicketReadOnlyDTO result = ticketService.insertTicket(dto, "user.demo");

        // Assert
        assertEquals("Laptop won't turn on", result.getTitle());
        verify(ticketRepository).save(any(Ticket.class));


    }

    @Test
    void insertTicket_shouldThrowWhenCategoryNotFound() {
        TicketInsertDTO dto = new TicketInsertDTO();
        dto.setTitle("Something broke");
        dto.setDescription("Details here");
        dto.setPriority(TicketPriority.LOW);
        dto.setCategoryId(999L);

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> ticketService.insertTicket(dto, "user.demo"));
    }

    @Test
    void assignTicket_firstTimeAssignment_shouldNotRequireReason() {
        Ticket ticket = new Ticket();
        ticket.setAssignedTo(null); // ήταν unassigned

        User agent = new User();
        agent.setFullName("James Park");

        User actor = new User();
        actor.setUsername("admin.demo");

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUuid("agent-uuid")).thenReturn(Optional.of(agent));
        when(userRepository.findByUsername("admin.demo")).thenReturn(Optional.of(actor));

        ticketService.assignTicket("t-uuid", "agent-uuid", null, "admin.demo");

        assertEquals(agent, ticket.getAssignedTo());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void assignTicket_reassignment_shouldRequireReason() {
        User previousAgent = new User();
        Ticket ticket = new Ticket();
        ticket.setAssignedTo(previousAgent); // ήταν ήδη ανατεθειμένο

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class,
                () -> ticketService.assignTicket("t-uuid", "agent-uuid", null, "admin.demo"));
    }

    @Test
    void assignTicket_reassignment_withReason_shouldSaveInternalComment() {
        User previousAgent = new User();
        Ticket ticket = new Ticket();
        ticket.setAssignedTo(previousAgent);

        User newAgent = new User();
        newAgent.setFullName("Maria Nikou");

        User actor = new User();
        actor.setUsername("admin.demo");

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUuid("agent-uuid")).thenReturn(Optional.of(newAgent));
        when(userRepository.findByUsername("admin.demo")).thenReturn(Optional.of(actor));

        ticketService.assignTicket("t-uuid", "agent-uuid", "Maria is on leave", "admin.demo");

        assertEquals(newAgent, ticket.getAssignedTo());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertTrue(captor.getValue().getBody().contains("Maria is on leave"));
        assertTrue(captor.getValue().isInternalNote());
    }


    @Test
    void updateTicket_byOwner_shouldUpdateContentButNotStatusOrAssignment() {
        User owner = new User();
        owner.setId(5L);
        owner.setUsername("user.demo");
        owner.setRole(Role.USER);

        Category category = new Category();
        category.setId(2L);

        Ticket ticket = new Ticket();
        ticket.setCreatedBy(owner);
        ticket.setStatus(TicketStatus.OPEN);

        TicketUpdateDTO dto = new TicketUpdateDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");
        dto.setPriority(TicketPriority.HIGH);
        dto.setCategoryId(2L);
        dto.setStatus(TicketStatus.CLOSED); // owner δεν πρέπει να μπορεί να το περάσει

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("user.demo")).thenReturn(Optional.of(owner));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        ticketService.updateTicket("t-uuid", dto, "user.demo");

        assertEquals("Updated title", ticket.getTitle());
        assertEquals(TicketStatus.OPEN, ticket.getStatus()); // ΔΕΝ άλλαξε
    }

    @Test
    void updateTicket_byStaff_shouldUpdateStatusAndAssignment() {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin.demo");
        admin.setRole(Role.ADMIN);

        User creator = new User();
        creator.setId(5L);

        User agent = new User();
        agent.setUsername("james.support");

        Category category = new Category();
        category.setId(2L);

        Ticket ticket = new Ticket();
        ticket.setCreatedBy(creator);
        ticket.setStatus(TicketStatus.OPEN);

        TicketUpdateDTO dto = new TicketUpdateDTO();
        dto.setTitle("Updated title");
        dto.setDescription("Updated description");
        dto.setPriority(TicketPriority.HIGH);
        dto.setCategoryId(2L);
        dto.setStatus(TicketStatus.RESOLVED);
        dto.setAssignedToId("agent-uuid");

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("admin.demo")).thenReturn(Optional.of(admin));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(userRepository.findByUuid("agent-uuid")).thenReturn(Optional.of(agent));

        ticketService.updateTicket("t-uuid", dto, "admin.demo");

        assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
        assertEquals(agent, ticket.getAssignedTo());
    }

    @Test
    void updateTicket_byStranger_shouldThrowAccessDenied() {
        User creator = new User();
        creator.setId(5L);

        User stranger = new User();
        stranger.setId(99L);
        stranger.setUsername("nina.user");
        stranger.setRole(Role.USER);

        Ticket ticket = new Ticket();
        ticket.setCreatedBy(creator);

        TicketUpdateDTO dto = new TicketUpdateDTO();

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("nina.user")).thenReturn(Optional.of(stranger));

        assertThrows(AccessDeniedException.class,
                () -> ticketService.updateTicket("t-uuid", dto, "nina.user"));
    }

    @Test
    void deleteTicket_shouldSetDeletedFlagTrue() {
        Ticket ticket = new Ticket();
        ticket.setDeleted(false);

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));

        ticketService.deleteTicket("t-uuid");

        assertTrue(ticket.isDeleted());
        verify(ticketRepository, never()).delete(any(Ticket.class));
        verify(ticketRepository, never()).deleteById(any());
    }

    @Test
    void findByUuid_shouldReturnDetailDtoWithComments() {
        Category category = new Category();
        category.setName("Network");

        Ticket ticket = new Ticket();
        ticket.setTitle("WiFi drops");
        ticket.setDescription("Every few minutes");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setCategory(category);

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicketAndDeletedFalseOrderByCreatedAtAsc(ticket))
                .thenReturn(List.of());
        when(attachmentRepository.findByTicketAndDeletedFalseOrderByCreatedAtAsc(ticket))
                .thenReturn(List.of());

        TicketDetailDTO result = ticketService.findByUuid("t-uuid");

        assertEquals("WiFi drops", result.getTitle());
        assertEquals("Network", result.getCategoryName());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void findPaginated_shouldMapResultsToReadOnlyDTO() {
        User currentUser = new User();
        currentUser.setUsername("admin.demo");
        currentUser.setRole(Role.ADMIN);

        Ticket ticket = new Ticket();
        ticket.setTitle("Sample ticket");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.MEDIUM);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> page = new PageImpl<>(List.of(ticket));

        when(userRepository.findByUsername("admin.demo")).thenReturn(Optional.of(currentUser));
        when(ticketRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<TicketReadOnlyDTO> result = ticketService.findPaginated(null, null, null, pageable, "admin.demo");

        assertEquals(1, result.getTotalElements());
        assertEquals("Sample ticket", result.getContent().get(0).getTitle());
    }

    @Test
    void findPaginated_shouldThrow_whenCurrentUserNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> ticketService.findPaginated(null, null, null, pageable, "ghost"));
    }


}