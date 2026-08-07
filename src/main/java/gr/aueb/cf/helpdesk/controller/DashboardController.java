package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.getStats());
        model.addAttribute("agentWorkload", dashboardService.getAgentWorkload());
        model.addAttribute("recentTickets", dashboardService.getRecentTickets());
        model.addAttribute("orphanedTickets", dashboardService.getOrphanedTickets());
        model.addAttribute("unassignedTickets", dashboardService.getUnassignedTickets());
        return "dashboard";
    }
}
