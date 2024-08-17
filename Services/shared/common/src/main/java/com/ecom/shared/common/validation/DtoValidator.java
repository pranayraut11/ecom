package com.ecom.shared.common.validation;

import com.ecom.shared.common.exception.BadRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class DtoValidator {

    @Autowired
    private Validator validator;

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {

            HashMap<String, String> error = new HashMap<>();
            for (ConstraintViolation<T> constraintViolation : violations) {
                StringBuilder sb = new StringBuilder();
                constraintViolation.getPropertyPath().forEach(path -> sb.append(path.getName() + "."));
                String path = sb.toString();
                error.put(path.substring(0, path.lastIndexOf(".")), constraintViolation.getMessage());
            }
            throw new BadRequest("Please provide valid data!", error);
        }
    }
}
