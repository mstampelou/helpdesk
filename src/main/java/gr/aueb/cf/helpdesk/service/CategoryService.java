package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.CategoryReadOnlyDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryReadOnlyDTO> findAllActive();
}
