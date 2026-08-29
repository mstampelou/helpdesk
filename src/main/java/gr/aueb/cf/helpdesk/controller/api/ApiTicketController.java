package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Tickets", description = "Paginated, filterable ticket listing")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class ApiTicketController {

    private final TicketService ticketService;

    @Operation(summary = "List tickets", description = "Returns a paginated, role-scoped list of tickets, optionally filtered by search term, status, or priority.")
    @GetMapping
    public Page<TicketReadOnlyDTO> list(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) TicketStatus status,
                                        @RequestParam(required = false) TicketPriority priority,
                                        @PageableDefault(size = 10) Pageable pageable,
                                        Authentication authentication) {
        return ticketService.findPaginated(search, status, priority, pageable, authentication.getName());
    }
}