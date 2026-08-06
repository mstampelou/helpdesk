package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.TagReadOnlyDTO;

import java.util.List;

public interface TagService {
    List<TagReadOnlyDTO> findAll();
}
