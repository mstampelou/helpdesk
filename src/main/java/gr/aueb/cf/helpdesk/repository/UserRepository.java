package gr.aueb.cf.helpdesk.repository;

import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
List<User> findByRoleInAndDeletedFalseAndActiveTrue(List<Role> roles);
    List<User> findByDeletedFalseOrderByUsername();

}
