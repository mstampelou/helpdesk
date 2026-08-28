package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.ZoneId;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow();

        long openCount;
        long inProgressCount;

        if (currentUser.getRole() == Role.USER) {
            // Plain USER: scope stats to their own tickets, consistent with the ticket list
            openCount = ticketRepository.countByDeletedFalseAndStatusAndCreatedBy(TicketStatus.OPEN, currentUser);
            inProgressCount = ticketRepository.countByDeletedFalseAndStatusAndCreatedBy(TicketStatus.IN_PROGRESS, currentUser);
        } else {
            // ADMIN/SUPPORT: company-wide view
            openCount = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.OPEN);
            inProgressCount = ticketRepository.countByDeletedFalseAndStatus(TicketStatus.IN_PROGRESS);
        }

        model.addAttribute("openCount", openCount);
        model.addAttribute("inProgressCount", inProgressCount);

        // "resolved today" — simple demo approximation over RESOLVED + CLOSED tickets updated since midnight
        long resolvedToday = ticketRepository.findByDeletedFalse(
                        org.springframework.data.domain.PageRequest.of(0, 200)).stream()
                .filter(t -> (t.getStatus() == TicketStatus.RESOLVED || t.getStatus() == TicketStatus.CLOSED)
                        && t.getUpdatedAt() != null
                        && t.getUpdatedAt().isAfter(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        && (currentUser.getRole() != Role.USER || t.getCreatedBy().getId().equals(currentUser.getId())))
                .count();
        model.addAttribute("resolvedTodayCount", resolvedToday);

        return "home";
    }
}