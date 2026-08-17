package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changeRole_shouldUpdateRole_whenUserExists() {
        User user = new User();
        user.setRole(Role.USER);

        when(userRepository.findByUuid("u-uuid")).thenReturn(Optional.of(user));

        userService.changeRole("u-uuid", Role.SUPPORT);

        assertEquals(Role.SUPPORT, user.getRole());
    }

    @Test
    void changeRole_shouldThrow_whenUserNotFound() {
        when(userRepository.findByUuid("missing-uuid")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changeRole("missing-uuid", Role.SUPPORT));
    }

    @Test
    void toggleActive_shouldFlipActiveFlag() {
        User user = new User();
        user.setActive(true);

        when(userRepository.findByUuid("u-uuid")).thenReturn(Optional.of(user));

        userService.toggleActive("u-uuid");

        assertFalse(user.isActive());
    }
}