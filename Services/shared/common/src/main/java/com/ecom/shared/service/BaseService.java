package com.ecom.shared.service;

import java.util.List;

public abstract class BaseService<T> {

    public abstract List<T> getAll() ;

    public abstract T get(String id);

    public abstract void delete(String id);

    public abstract T create(T entity);

    public abstract T update(T entity);

}
