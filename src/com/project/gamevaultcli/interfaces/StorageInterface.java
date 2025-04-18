package com.project.gamevaultcli.interfaces;

import java.util.List;

public interface StorageInterface<T, ID> {
    T findById(ID id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(ID id);
}
