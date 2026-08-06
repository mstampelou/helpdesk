package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.UserRowDTO;
import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserRowDTO> findAll(String search) {
        List<User> users = userRepository.findByDeletedFalseOrderByUsername();

        return users.stream()
                .filter(u -> search == null || search.isBlank()
                        || u.getUsername().toLowerCase().contains(search.toLowerCase())
                        || u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .map(u -> new UserRowDTO(
                        u.getUuid(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole(),
                        u.isActive(),
                        ticketRepository.countByDeletedFalseAndCreatedBy(u),
                        u.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // These two are ADMIN-only at the route level already (SecurityConfig:
    // /admin/** -> hasRole('ADMIN')), but @PreAuthorize here is the
    // Week-3 method-level layer: it protects the service even if it were
    // ever called from somewhere else that isn't behind that route guard.
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void changeRole(String uuid, Role role) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        user.setRole(role); // managed entity — dirty checking saves this, no explicit save() needed
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void toggleActive(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        user.setActive(!user.isActive());
    }

    @Override
    @Transactional(readOnly = true)
    public List<gr.aueb.cf.helpdesk.dto.UserOptionDTO> findAgents() {
        return userRepository.findByRoleInAndDeletedFalse(List.of(Role.SUPPORT)).stream()
                .map(u -> new gr.aueb.cf.helpdesk.dto.UserOptionDTO(u.getUuid(), u.getFullName()))
                .collect(Collectors.toList());
    }
}
