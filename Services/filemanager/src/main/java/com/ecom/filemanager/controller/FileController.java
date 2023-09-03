package com.ecom.filemanager.controller;

import com.ecom.filemanager.dto.FileUploadDTO;
import com.ecom.filemanager.service.specification.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("files")
@Slf4j
public class FileController {


    @Autowired
    private FileService fileService;

    @PostMapping()
    public List<String> uploadFiles(MultipartFile[] files, String path) throws IOException {
        FileUploadDTO fileUploadDTO = new FileUploadDTO(List.of(files), path);
        List<String> filesUrl = fileService.uploadFiles(fileUploadDTO);
        return filesUrl;
    }

    @GetMapping(value = "/{path}/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void getFile(@PathVariable String fileId, @PathVariable String path, HttpServletResponse response) throws IOException {
        InputStream file = fileService.getFile(path, fileId);
        if(Objects.nonNull(file)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(file, response.getOutputStream());
        }else{
            log.info("File {} not found on file server on path {} ",fileId,path);
        }
    }

    @DeleteMapping()
    public void deleteFiles(@RequestBody List<String> ids,@RequestParam String folderName) throws IOException {
        fileService.deleteFiles(ids, folderName);
    }
}