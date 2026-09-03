package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.UserRowDTO;
import gr.aueb.cf.helpdesk.exception.UserNotFoundException;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void changeRole(String uuid, Role role) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        user.setRole(role); // managed entity — dirty checking saves this, no explicit save() needed
        log.info("User {} role changed to {}", uuid, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void toggleActive(String uuid, String currentUsername) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("You cannot disable your own account.");
        }
        user.setActive(!user.isActive());
        log.info("User {} active status toggled to {}", uuid, user.isActive());
    }

    @Override
    @Transactional(readOnly = true)
    public List<gr.aueb.cf.helpdesk.dto.UserOptionDTO> findAgents() {
        return userRepository.findByRoleInAndDeletedFalseAndActiveTrue(List.of(Role.SUPPORT)).stream()
                .map(u -> new gr.aueb.cf.helpdesk.dto.UserOptionDTO(u.getUuid(), u.getFullName()))
                .collect(Collectors.toList());
    }
}
