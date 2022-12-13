package com.ecom.user.entity;

import com.ecom.user.dto.User;
import com.ecom.user.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Table;
import java.util.List;


@Document("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMongo extends User {

    private List<Address> addresses;

}