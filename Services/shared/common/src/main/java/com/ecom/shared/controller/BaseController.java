package com.ecom.shared.controller;

import com.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<T> {

    @Autowired
    private BaseService<T> baseService;

    @GetMapping
    public List<T> getAll() {
        return baseService.getAll();
    }

    @GetMapping("{id}")
    public T get(@PathVariable String id){
        return baseService.get(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        baseService.delete(id);
    }

    @PostMapping
    public T create(@RequestBody T entity){
        return baseService.create(entity);
    }

    @PatchMapping
    public T update(@RequestBody T entity){
        return baseService.update(entity);
    }

}
