package com.epam.spring.repository;

import java.util.List;
import java.util.Optional;

public interface BaseOperationsRepository<ENTITY_TYPE> {

    ENTITY_TYPE create(ENTITY_TYPE entity);
    List<ENTITY_TYPE> findAll();
    Optional<ENTITY_TYPE> findById(Long id);
}
