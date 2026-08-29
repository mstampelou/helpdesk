package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.dto.TagReadOnlyDTO;
import gr.aueb.cf.helpdesk.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Tags", description = "Read-only access to ticket tags")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class ApiTagController {

    private final TagService tagService;

    @Operation(summary = "List all tags", description = "Returns all available ticket tags.")
    @GetMapping
    public List<TagReadOnlyDTO> list() {
        return tagService.findAll();
    }
}