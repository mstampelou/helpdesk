package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.config.SecurityConfig;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.security.AppUserDetailsService;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationFailureHandler;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationSuccessHandler;
import gr.aueb.cf.helpdesk.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiTicketController.class)
@Import(SecurityConfig.class)
class ApiTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private AppUserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Test
    @WithMockUser(username = "admin.demo")
    void list_shouldReturnPaginatedTickets() throws Exception {
        TicketReadOnlyDTO ticket = new TicketReadOnlyDTO(
                "t-uuid", "WiFi drops", TicketStatus.OPEN, TicketPriority.MEDIUM,
                null, List.of("vpn"), null);
        Page<TicketReadOnlyDTO> page = new PageImpl<>(List.of(ticket));

        when(ticketService.findPaginated(any(), any(), any(), any(Pageable.class), eq("admin.demo")))
                .thenReturn(page);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].uuid").value("t-uuid"))
                .andExpect(jsonPath("$.content[0].title").value("WiFi drops"));
    }

    @Test
    void list_withoutAuthentication_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}