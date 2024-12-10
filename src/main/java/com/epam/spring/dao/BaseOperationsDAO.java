package com.epam.spring.dao;

import java.util.List;

public interface BaseOperationsDAO<K, V> {

    K create(K entity);
    List<K> findAll();
    K findById(V uuid);
}
