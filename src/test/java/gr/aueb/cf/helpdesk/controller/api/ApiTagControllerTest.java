package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.config.SecurityConfig;
import gr.aueb.cf.helpdesk.dto.TagReadOnlyDTO;
import gr.aueb.cf.helpdesk.security.AppUserDetailsService;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationFailureHandler;
import gr.aueb.cf.helpdesk.security.CustomAuthenticationSuccessHandler;
import gr.aueb.cf.helpdesk.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiTagController.class)
@Import(SecurityConfig.class)
class ApiTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private AppUserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Test
    @WithMockUser
    void list_shouldReturnAllTags() throws Exception {
        TagReadOnlyDTO tag = new TagReadOnlyDTO(1L, "vpn");
        when(tagService.findAll()).thenReturn(List.of(tag));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("vpn"));
    }

    @Test
    void list_withoutAuthentication_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}