package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.UserOptionDTO;
import gr.aueb.cf.helpdesk.dto.UserRowDTO;
import gr.aueb.cf.helpdesk.model.enums.Role;

import java.util.List;

public interface UserService {
    List<UserRowDTO> findAll(String search);
    void changeRole(String uuid, Role role);
    void toggleActive(String uuid, String currentUsername);
    List<UserOptionDTO> findAgents();
}
