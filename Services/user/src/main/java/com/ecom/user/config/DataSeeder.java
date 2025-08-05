package com.ecom.user.config;

import com.ecom.user.entity.UserDetails;
import com.ecom.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) throws Exception {
       // userRepository.deleteAll();
//        List<UserDetails> existingUsers = new ArrayList<>();
//        int batchSize = 1000;
//        for (int i = 0; i < 9000000; i++) {
//
//            UserDetails user = new UserDetails();
//            user.setEmail("user" + i + "@example.com");
//            user.setFirstName("User" + i);
//            user.setLastName("Last" + i);
//            user.setUserId("user" + i);
//            user.setEnabled(true);
//            if(existingUsers.size() == batchSize) {
//                userRepository.saveAll(existingUsers);
//                existingUsers.clear();
//            }
//            existingUsers.add(user);
//        }
//
//    }
}}
