package com.epam.spring.dao;

public interface ExtendedOperationsDAO<ENTITY_TYPE> {

    void delete(ENTITY_TYPE entity);
    ENTITY_TYPE update(ENTITY_TYPE entity);
}
