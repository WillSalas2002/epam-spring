package com.epam.spring.service;

import java.util.List;

public interface BaseOperationsService<ENTITY_TYPE> {

    List<ENTITY_TYPE> findAll();
    ENTITY_TYPE findById(Long id);
    ENTITY_TYPE create(ENTITY_TYPE entity);
}
