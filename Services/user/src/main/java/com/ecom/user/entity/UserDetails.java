package com.ecom.user.entity;

import com.ecom.user.dto.User;
import com.ecom.user.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Document("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails extends User {

    private String userId;

    private Set<Address> addresses;

}