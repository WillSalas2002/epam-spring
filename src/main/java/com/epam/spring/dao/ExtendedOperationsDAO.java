package com.epam.spring.dao;

public interface ExtendedOperationsDAO <K> {

    void delete(K entity);
    K update(K entity);
}
