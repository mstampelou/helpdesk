package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Dashboard", description = "Read-only dashboard statistics")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ApiDashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get ticket statistics", description = "Returns counts by status and priority across all non-deleted tickets.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPPORT')")
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return dashboardService.getStats();
    }
}
