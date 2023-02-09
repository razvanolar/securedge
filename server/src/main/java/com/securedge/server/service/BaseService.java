package com.securedge.server.service;

import java.util.List;

public interface BaseService<T> {

    List<T> findAll();

    T findById(int id);

    T create(T model);

    T update(T model);

    void delete(int id);
}
