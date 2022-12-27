package com.ecom.user.repository;

import com.ecom.user.dto.User;
import com.ecom.user.entity.UserMongo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserMongo,String> {
}