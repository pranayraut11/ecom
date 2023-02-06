package com.ecom.user.utils;

import com.ecom.shared.exception.EcomException;
import com.ecom.user.model.Credential;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public final class UserUtils {

    private UserUtils() {
    }

    private static final String PASSWORD = "password";

    public static String getPassword(@NotEmpty List<Credential> credentials){
       return credentials.stream().filter(credential -> credential.getType().equalsIgnoreCase(PASSWORD)).collect(Collectors.toList()).stream().findFirst().orElseThrow(()-> new EcomException(HttpStatus.BAD_REQUEST,"404")).getValue();
    }
}
