package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.config.SecurityConfig;
import gr.aueb.cf.helpdesk.security.AppUserDetailsService;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationFailureHandler;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationSuccessHandler;
import gr.aueb.cf.helpdesk.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiDashboardController.class)
@Import(SecurityConfig.class)
class ApiDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private AppUserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStats_asAdmin_shouldReturnStats() throws Exception {
        when(dashboardService.getStats()).thenReturn(Map.of("open", 5, "closed", 12));

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.open").value(5))
                .andExpect(jsonPath("$.closed").value(12));
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void getStats_asSupport_shouldReturnStats() throws Exception {
        when(dashboardService.getStats()).thenReturn(Map.of("open", 5));

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStats_asPlainUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStats_withoutAuthentication_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}