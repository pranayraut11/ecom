package com.ecom.filemanager.integration.controller;

import com.ecom.filemanager.controller.FileController;
import com.ecom.filemanager.integration.ContainerConfig;
import com.ecom.shared.common.utility.FileUtility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Import(ContainerConfig.class)
public class FileControllerTest {

    @Autowired
    private FileController fileController;

    @Test
    void testCreateTest() throws IOException {
        MultipartFile file = FileUtility.getMultipartFile("/images/tv.jpg");
        fileController.uploadFiles((MultipartFile[]) List.of().toArray(),"basket");
    }
}
