package com.epam.spring.service;

import java.util.List;

public interface BaseService <K, V> {

    List<K> findAll();
    K findById(V uuid);
    K create(K entity);
}
