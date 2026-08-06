package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.helpdesk.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryReadOnlyDTO> findAllActive() {
        return categoryRepository.findByActiveTrueAndDeletedFalse().stream()
                .map(c -> new CategoryReadOnlyDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }
}
