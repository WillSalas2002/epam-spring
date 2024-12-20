package com.epam.spring.dao;

import java.util.List;
import java.util.UUID;

public interface BaseOperationsDAO<ENTITY_TYPE> {

    ENTITY_TYPE create(ENTITY_TYPE entity);
    List<ENTITY_TYPE> findAll();
    ENTITY_TYPE findById(UUID uuid);
}
