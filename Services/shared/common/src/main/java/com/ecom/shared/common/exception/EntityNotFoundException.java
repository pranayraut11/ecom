package com.ecom.shared.common.exception;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    private HttpStatus statusCode;

    private String errorCode;

    private String message;

    public EntityNotFoundException(HttpStatus statusCode, String errorCode, String message, String... parameters) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = buildFinalMessage(message,parameters);

    }

    private String buildFinalMessage(@NotEmpty String message, String... parameters) {
        String[] words = message.split(" ");
        StringBuilder finalString = new StringBuilder();
        List<String> replacementList = Arrays.stream(words).filter(replacement -> replacement.equals("{}")).toList();
        if (!replacementList.isEmpty() && replacementList.size() == parameters.length) {
            int counter = 0;
            for(String word :words) {
                if(word.equals("{}")){
                    word = parameters[counter];
                    counter++;
                }
                finalString.append(word);
            }
        }
       return finalString.toString();
    }
}
