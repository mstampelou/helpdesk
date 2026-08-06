package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.CommentReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TicketDetailDTO;
import gr.aueb.cf.helpdesk.dto.TicketInsertDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TicketUpdateDTO;
import gr.aueb.cf.helpdesk.exception.CategoryNotFoundException;
import gr.aueb.cf.helpdesk.exception.TicketNotFoundException;
import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.Category;
import gr.aueb.cf.helpdesk.model.Comment;
import gr.aueb.cf.helpdesk.model.Tag;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.repository.CategoryRepository;
import gr.aueb.cf.helpdesk.repository.CommentRepository;
import gr.aueb.cf.helpdesk.repository.TagRepository;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import gr.aueb.cf.helpdesk.specification.TicketSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public TicketReadOnlyDTO insertTicket(TicketInsertDTO dto, String currentUsername) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));
        User creator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(currentUsername));

        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setCategory(category);
        ticket.setCreatedBy(creator);
        ticket.setTags(resolveTags(dto.getTagIds()));

        Ticket saved = ticketRepository.save(ticket);
        return toReadOnlyDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketReadOnlyDTO> findPaginated(String search, TicketStatus status, TicketPriority priority, Pageable pageable) {
        Specification<Ticket> spec = Specification.where(TicketSpecifications.notDeleted())
                .and(TicketSpecifications.hasStatus(status))
                .and(TicketSpecifications.hasPriority(priority))
                .and(TicketSpecifications.titleContains(search));

        return ticketRepository.findAll(spec, pageable).map(this::toReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDetailDTO findByUuid(String uuid) {
        Ticket ticket = getTicketOrThrow(uuid);

        List<CommentReadOnlyDTO> comments = commentRepository
                .findByTicketAndDeletedFalseOrderByCreatedAtAsc(ticket).stream()
                .map(c -> new CommentReadOnlyDTO(
                        c.getAuthor().getInitials(),
                        c.getAuthor().getFullName(),
                        c.getAuthor().getRole(),
                        c.getCreatedAt(),
                        c.isInternalNote(),
                        c.getBody()))
                .collect(Collectors.toList());

        return new TicketDetailDTO(
                ticket.getUuid(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory() != null ? ticket.getCategory().getName() : null,
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getFullName() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                comments
        );
    }

    @Override
    @Transactional
    public void addComment(String ticketUuid, String body, boolean internalNote, String currentUsername) {
        Ticket ticket = getTicketOrThrow(ticketUuid);
        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(currentUsername));

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setBody(body);
        comment.setInternalNote(internalNote);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketUpdateDTO getForEdit(String uuid) {
        Ticket ticket = getTicketOrThrow(uuid);

        TicketUpdateDTO dto = new TicketUpdateDTO();
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setCategoryId(ticket.getCategory() != null ? ticket.getCategory().getId() : null);
        dto.setStatus(ticket.getStatus());
        dto.setAssignedToId(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUuid() : null);
        dto.setTagIds(ticket.getTags().stream().map(Tag::getId).collect(Collectors.toList()));
        return dto;
    }

    // No @PreAuthorize here on purpose: field-level authorization instead of
    // method-level, since a plain USER is allowed to edit their own ticket's
    // content (title/description/priority/category) but not its
    // status/assignment — those two fields are silently ignored unless the
    // caller is ADMIN/SUPPORT. See assignTicket() below for the
    // method-level-restricted alternative.
    @Override
    @Transactional
    public void updateTicket(String uuid, TicketUpdateDTO dto, String currentUsername) {
        Ticket ticket = getTicketOrThrow(uuid);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(currentUsername));

        boolean isStaff = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SUPPORT;
        boolean isOwner = ticket.getCreatedBy().getId().equals(currentUser.getId());

        if (!isStaff && !isOwner) {
            throw new AccessDeniedException("Only the ticket's creator or staff can edit this ticket.");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(dto.getPriority());
        ticket.setCategory(category);
        ticket.setTags(resolveTags(dto.getTagIds()));

        if (isStaff) {
            if (dto.getStatus() != null) {
                ticket.setStatus(dto.getStatus());
            }
            if (dto.getAssignedToId() != null && !dto.getAssignedToId().isBlank()) {
                User agent = userRepository.findByUuid(dto.getAssignedToId())
                        .orElseThrow(() -> new UserNotFoundException(dto.getAssignedToId()));
                ticket.setAssignedTo(agent);
            } else {
                ticket.setAssignedTo(null);
            }
        }
        // managed entity — dirty checking persists this, no explicit save() needed
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    @Override
    @Transactional
    public void deleteTicket(String uuid) {
        Ticket ticket = getTicketOrThrow(uuid);
        ticket.setDeleted(true); // soft delete — never a real DB delete
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    @Override
    @Transactional
    public void assignTicket(String uuid, String agentUuid) {
        Ticket ticket = getTicketOrThrow(uuid);
        User agent = userRepository.findByUuid(agentUuid)
                .orElseThrow(() -> new UserNotFoundException(agentUuid));
        ticket.setAssignedTo(agent);
    }

    private Ticket getTicketOrThrow(String uuid) {
        return ticketRepository.findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new TicketNotFoundException(uuid));
    }

    private Set<Tag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(tagRepository.findAllById(tagIds));
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
