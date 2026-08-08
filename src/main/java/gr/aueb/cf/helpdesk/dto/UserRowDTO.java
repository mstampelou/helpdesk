package gr.aueb.cf.helpdesk.dto;

import gr.aueb.cf.helpdesk.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRowDTO {
    private String uuid;
    private String username;
    private String email;
    private Role role;
    private boolean active;
    private long ticketCount;
    private Instant createdAt;
}
