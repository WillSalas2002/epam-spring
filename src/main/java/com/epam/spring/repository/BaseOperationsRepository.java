package com.epam.spring.repository;

import java.util.List;

public interface BaseOperationsRepository<ENTITY_TYPE> {

    ENTITY_TYPE create(ENTITY_TYPE entity);
    List<ENTITY_TYPE> findAll();
    ENTITY_TYPE findById(Long id);
}
