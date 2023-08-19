package com.ecom.product.utility;

import jakarta.validation.constraints.NotEmpty;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtility {
    static ClassLoader classLoader = FileUtility.class.getClassLoader();

    public static String getJsonFromFile(@NotEmpty String fileName){
       return getJson( new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile()));
    }

    private static String getJson(File file) {

        String json = null;
        try {
            json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
