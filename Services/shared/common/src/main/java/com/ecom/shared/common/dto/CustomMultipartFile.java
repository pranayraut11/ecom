package com.ecom.shared.common.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

public class CustomMultipartFile implements MultipartFile {

    private final byte[] input;
    private File file = null;
    private final String originalFileName;
    private String pathName;

    public CustomMultipartFile(File file) throws IOException {
        this.originalFileName = file.getAbsolutePath();
        this.file = file;
        input = Files.readAllBytes(file.toPath());
    }

    @Override
    public String getName() {
        return this.file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFileName;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File destination) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(input);
        }
    }
}