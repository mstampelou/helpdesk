package gr.aueb.cf.helpdesk.controller.api;

import gr.aueb.cf.helpdesk.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.helpdesk.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Categories", description = "Read-only access to ticket categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "List active categories", description = "Returns all non-deleted, active ticket categories.")
    @GetMapping
    public List<CategoryReadOnlyDTO> list() {
        return categoryService.findAllActive();
    }
}
