package com.ecom.filemanager.integration;

import com.ecom.filemanager.FilemanagerApplication;
import org.springframework.boot.SpringApplication;

public class TestMyApplication {

    public static void main(String[] args) {
        SpringApplication.from(FilemanagerApplication::main).with(ContainerConfig.class).run(args);
    }

}