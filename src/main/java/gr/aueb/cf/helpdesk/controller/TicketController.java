package gr.aueb.cf.helpdesk.controller;

import gr.aueb.cf.helpdesk.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TagReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TicketDetailDTO;
import gr.aueb.cf.helpdesk.dto.TicketInsertDTO;
import gr.aueb.cf.helpdesk.dto.TicketReadOnlyDTO;
import gr.aueb.cf.helpdesk.dto.TicketUpdateDTO;
import gr.aueb.cf.helpdesk.dto.UserOptionDTO;
import gr.aueb.cf.helpdesk.model.enums.TicketPriority;
import gr.aueb.cf.helpdesk.model.enums.TicketStatus;
import gr.aueb.cf.helpdesk.service.CategoryService;
import gr.aueb.cf.helpdesk.service.TagService;
import gr.aueb.cf.helpdesk.service.TicketService;
import gr.aueb.cf.helpdesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                        @RequestParam(required = false) TicketStatus status,
                        @RequestParam(required = false) TicketPriority priority,
                        @PageableDefault(size = 10) Pageable pageable,
                        Model model) {

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "updatedAt"));
        }

        Page<TicketReadOnlyDTO> tickets = ticketService.findPaginated(search, status, priority, pageable);

        model.addAttribute("tickets", tickets);
        model.addAttribute("search", search);
        model.addAttribute("statusFilter", status != null ? status.name() : null);
        model.addAttribute("priorityFilter", priority != null ? priority.name() : null);
        model.addAttribute("activeCount", tickets.getContent().stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN || t.getStatus() == TicketStatus.IN_PROGRESS)
                .count());
        return "tickets/list";
    }

    @GetMapping("/{uuid}")
    public String detail(@PathVariable String uuid, Model model) {
        TicketDetailDTO ticket = ticketService.findByUuid(uuid);
        model.addAttribute("ticket", ticket);
        return "tickets/detail";
    }

    @PostMapping("/{uuid}/comments")
    public String addComment(@PathVariable String uuid,
                              @RequestParam String body,
                              @RequestParam(required = false, defaultValue = "false") boolean internalNote,
                              Authentication authentication) {
        ticketService.addComment(uuid, body, internalNote, authentication.getName());
        return "redirect:/tickets/" + uuid;
    }

    @PostMapping("/{uuid}/assign")
    public String assign(@PathVariable String uuid, @RequestParam String agentUuid) {
        ticketService.assignTicket(uuid, agentUuid);
        return "redirect:/tickets/" + uuid;
    }

    @PostMapping("/{uuid}/delete")
    public String delete(@PathVariable String uuid) {
        ticketService.deleteTicket(uuid);
        return "redirect:/tickets";
    }

    @GetMapping("/{uuid}/edit")
    public String getEditForm(@PathVariable String uuid, Model model) {
        model.addAttribute("ticketUpdateDTO", ticketService.getForEdit(uuid));
        model.addAttribute("uuid", uuid);
        return "tickets/edit";
    }

    @PostMapping("/{uuid}/edit")
    public String updateTicket(@PathVariable String uuid,
                                @Valid @ModelAttribute("ticketUpdateDTO") TicketUpdateDTO ticketUpdateDTO,
                                BindingResult bindingResult,
                                Model model,
                                Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("uuid", uuid);
            return "tickets/edit";
        }
        ticketService.updateTicket(uuid, ticketUpdateDTO, authentication.getName());
        return "redirect:/tickets/" + uuid;
    }

    @GetMapping("/insert")
    public String getTicketForm(Model model) {
        model.addAttribute("ticketInsertDTO", TicketInsertDTO.empty());
        return "tickets/insert";
    }

    @PostMapping("/insert")
    public String insertTicket(@Valid @ModelAttribute("ticketInsertDTO") TicketInsertDTO ticketInsertDTO,
                                BindingResult bindingResult,
                                Model model,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "tickets/insert";
        }

        TicketReadOnlyDTO ticketReadOnlyDTO;
        try {
            ticketReadOnlyDTO = ticketService.insertTicket(ticketInsertDTO, authentication.getName());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not create ticket: " + e.getMessage());
            return "tickets/insert";
        }

        redirectAttributes.addFlashAttribute("ticketReadOnlyDTO", ticketReadOnlyDTO);
        return "redirect:/tickets/success";
    }

    @GetMapping("/success")
    public String ticketSuccess() {
        return "tickets/success";
    }

    @ModelAttribute("categoriesReadOnlyDTO")
    public List<CategoryReadOnlyDTO> categories() {
        return categoryService.findAllActive();
    }

    @ModelAttribute("tagsReadOnlyDTO")
    public List<TagReadOnlyDTO> tags() {
        return tagService.findAll();
    }

    @ModelAttribute("agentsReadOnlyDTO")
    public List<UserOptionDTO> agents() {
        return userService.findAgents();
    }
}
