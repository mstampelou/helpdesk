package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final TicketRepository ticketRepository;

    @GetMapping("/")
    public String landing() {
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("openCount", ticketRepository.countByDeletedFalseAndStatus(TicketStatus.OPEN));
        model.addAttribute("inProgressCount", ticketRepository.countByDeletedFalseAndStatus(TicketStatus.IN_PROGRESS));
        // "resolved today" — simple demo approximation over RESOLVED tickets updated since midnight
        long resolvedToday = ticketRepository.findByDeletedFalse(
                        org.springframework.data.domain.PageRequest.of(0, 200)).stream()
                .filter(t -> t.getStatus() == TicketStatus.RESOLVED
                        && t.getUpdatedAt() != null
                        && t.getUpdatedAt().isAfter(LocalDateTime.of(LocalDate.now(), java.time.LocalTime.MIN)))
                .count();
        model.addAttribute("resolvedTodayCount", resolvedToday);
        return "home";
    }
}
