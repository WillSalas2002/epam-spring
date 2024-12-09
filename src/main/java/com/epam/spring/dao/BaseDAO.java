package com.epam.spring.dao;

import java.util.List;

public interface BaseDAO<K, V> {

    K create(K entity);
    List<K> findAll();
    K findById(V uuid);
}
