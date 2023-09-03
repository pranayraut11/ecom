package com.ecom.shared.common.utility;

import com.ecom.shared.common.dto.CustomMultipartFile;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
public class FileUtility {
    static ClassLoader classLoader = FileUtility.class.getClassLoader();

    public static File getFile(@NotEmpty String fileName) {
        return new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
    }

    public static MultipartFile getMultipartFile(@NotEmpty String fileName) throws IOException {
        return new CustomMultipartFile(getFile(fileName));
    }

    public static String getJson(@NotEmpty String fileName) {
        String json = null;
        try {
            json = Files.readString(getFile(fileName).toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info(e.getLocalizedMessage());
        }
        return json;
    }
}
