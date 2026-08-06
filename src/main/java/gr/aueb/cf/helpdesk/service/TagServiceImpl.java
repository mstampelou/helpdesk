package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.TagReadOnlyDTO;
import gr.aueb.cf.helpdesk.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TagReadOnlyDTO> findAll() {
        return tagRepository.findByDeletedFalseOrderByName().stream()
                .map(t -> new TagReadOnlyDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
}
