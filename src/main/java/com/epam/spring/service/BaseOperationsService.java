package com.epam.spring.service;

import java.util.List;
import java.util.UUID;

public interface BaseOperationsService<ENTITY_TYPE> {

    List<ENTITY_TYPE> findAll();
    ENTITY_TYPE findById(UUID uuid);
    ENTITY_TYPE create(ENTITY_TYPE entity);
}
