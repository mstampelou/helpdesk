package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.dto.UserRowDTO;
import gr.aueb.cf.helpdesk.model.enums.Role;
import gr.aueb.cf.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        List<UserRowDTO> users = userService.findAll(search);
        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("activeCount", users.stream().filter(UserRowDTO::isActive).count());
        model.addAttribute("adminCount", users.stream().filter(u -> u.getRole() == Role.ADMIN).count());
        model.addAttribute("supportCount", users.stream().filter(u -> u.getRole() == Role.SUPPORT).count());
        model.addAttribute("userCount", users.stream().filter(u -> u.getRole() == Role.USER).count());
        return "admin/users";
    }

    @PostMapping("/{uuid}/role")
    public String changeRole(@PathVariable String uuid, @RequestParam Role role) {
        userService.changeRole(uuid, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/{uuid}/toggle-active")
    public String toggleActive(@PathVariable String uuid) {
        userService.toggleActive(uuid);
        return "redirect:/admin/users";
    }
}
