package com.ecom.user.repository;

import com.ecom.user.entity.UserDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserDetails,String> {

    Optional<UserDetails> findByUserId(String userID);

    boolean existsByEmail(String email);

    boolean findByEmailIn(String userId);

}
